package com.readforce.authentication.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.authentication.exception.AuthenticationException;
import com.readforce.authentication.service.AuthenticationService;
import com.readforce.authentication.util.JwtUtil;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.ExpireTime;
import com.readforce.common.enums.Name;
import com.readforce.common.enums.Prefix;
import com.readforce.common.exception.JsonException;
import com.readforce.member.dto.MemberSignInDto;
import com.readforce.member.dto.MemberSocialProviderDto;
import com.readforce.member.dto.MemberSummaryDto;
import com.readforce.member.service.AttendanceService;
import com.readforce.member.service.MemberService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

	@Value("${custom.fronted.kakao-logout-url}")
	private String kakaoLogoutUrl;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;
	
	@Value("${custom.fronted.logout-redirect-url}")
	private String logoutRedirectUrl;
	
	private final AuthenticationManager authenticationManager;
	private final AttendanceService attendanceService;
	private final AuthenticationService authenticationService;
	private final JwtUtil jwtUtil;
	private final MemberService memberService;
	private final StringRedisTemplate redisTemplate;
	
	@PostMapping("/sign-in")
	public ResponseEntity<Map<String, String>> signIn(@Valid @RequestBody MemberSignInDto memberSignInDto){
		
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(memberSignInDto.getEmail(), memberSignInDto.getPassword())	
		);
		
		attendanceService.recordAttendance(memberSignInDto.getEmail());
		
		final UserDetails userDetails = authenticationService.loadUserByUsername(memberSignInDto.getEmail());
		final String accessToken = jwtUtil.generateAccessToken(userDetails);
		final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
		
		authenticationService.storeRefreshToken(userDetails.getUsername(), refreshToken);
				
		MemberSummaryDto memberSummaryDto = memberService.getActiveMemberByEmailWithMemberSummaryDto(userDetails.getUsername());
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				Name.ACCESS_TOKEN.name(), accessToken,
				Name.REFRESH_TOKEN.name(), refreshToken,
				Name.NICKNAME.name(), memberSummaryDto.getNickname(),
				Name.SOCIAL_PROVIDER.name(), memberSummaryDto.getSocialProvider() == null ? "" : memberSummaryDto.getSocialProvider(),
				MessageCode.MESSAGE_CODE, MessageCode.SIGN_IN_SUCCESS
		));
	
	}
	
	@DeleteMapping("/sign-out")
	public ResponseEntity<Map<String, String>> signOut(@AuthenticationPrincipal UserDetails userDetails){
		
		String email = userDetails.getUsername();
		
		authenticationService.deleteRefreshToken(email);
		
		MemberSocialProviderDto memberSocialProviderDto = memberService.getActiveMemberByEmailWithMemberSoicalProviderDto(email);
		String socialProvider = memberSocialProviderDto.getSocialProvider();
		
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put(MessageCode.MESSAGE_CODE, MessageCode.SIGN_OUT_SUCCESS);
		
		if("kakao".equals(socialProvider)) {
			
			String kakaoSignOutUrl = kakaoLogoutUrl
					+ kakaoClientId
					+ "&logout_redirect_uri="
					+ logoutRedirectUrl;
			
			responseBody.put(Name.KAKAO_SIGN_OUT_URL.name(), kakaoSignOutUrl);
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);		
		
	}
	
	@PostMapping("/reissue-refresh-token")
	public ResponseEntity<Map<String, String>> reissueRefreshToken(
    		@RequestParam("refreshToken")
    		@NotBlank(message = MessageCode.REFRESH_TOKEN_NOT_BLANK)
    		String refreshToken
	){
		
		String username = jwtUtil.extractUsername(refreshToken);
		String stroedRefreshToken = authenticationService.getRefreshToken(username);
		
		if(stroedRefreshToken == null) {
			
			log.info("요청받은 리프레쉬 토큰: {}", refreshToken);
			
			authenticationService.deleteRefreshToken(username);
			
			log.warn("보안 경고: 유효하지 않은 리프레쉬 토큰 사용 시도. 사용자 {}", username);
			
			throw new AuthenticationException(MessageCode.AUTHENTICATION_FAIL);
			
		}
		
		if(!stroedRefreshToken.equals(refreshToken) || jwtUtil.isExpiredToken(stroedRefreshToken)) {
			
			throw new AuthenticationException(MessageCode.AUTHENTICATION_FAIL);
			
		}
		
		final UserDetails userDetails = authenticationService.loadUserByUsername(username);
		final String newAccessToken = jwtUtil.generateAccessToken(userDetails);
		final String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
		
		authenticationService.storeRefreshToken(username, newRefreshToken);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				Name.ACCESS_TOKEN.name(), newAccessToken,
				Name.REFRESH_TOKEN.name(), newRefreshToken,
				MessageCode.MESSAGE_CODE, MessageCode.REISSUE_ACCESS_TOKEN_SUCCESS				
		));
		
	}
	
	@PostMapping("/get-tokens")
	public ResponseEntity<Map<String, String>> getTokens(
			@RequestParam("temporalToken")
			@NotBlank(message = MessageCode.TEMPORAL_TOKEN_NOT_BLANK)
			String temporalToken
	){
		
		String temporalTokenJson = (String)redisTemplate.opsForValue().get(Prefix.TEMPORAL.getContent() + temporalToken);
		
		if(temporalTokenJson == null) {
			
			throw new AuthenticationException(MessageCode.AUTHENTICATION_FAIL);
			
		}
		
		redisTemplate.delete(Prefix.TEMPORAL.getContent() + temporalToken);
		
		Map<String, String> tokenMap;
		
		try {
			
			tokenMap = new ObjectMapper().readValue(temporalTokenJson, new TypeReference<Map<String, String>>() {});
			tokenMap.put(MessageCode.MESSAGE_CODE, MessageCode.GET_TOKENS_SUCCESS);
			
		} catch(JsonMappingException exception) {
			
			throw new JsonException(MessageCode.JSON_MAPPING_FAIL);
			
		} catch(JsonProcessingException exception) {
			
			throw new JsonException(MessageCode.JSON_PROCESSING_FAIL);
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(tokenMap);
		
	}
	
	@PostMapping("/get-social-account-link-token")
	public ResponseEntity<Map<String, String>> getSocialAccountLinkToken(@AuthenticationPrincipal UserDetails userDetails){
		
		String email = userDetails.getUsername();
		
		String state = UUID.randomUUID().toString();
		
		redisTemplate.opsForValue().set(
				Prefix.SOCIAL_LINK_STATE.getContent() + state,
				email,
				Duration.ofMinutes(ExpireTime.SOCIAL_ACCOUNT_LINK.getTime())
				);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(Name.STATE.name(), state));
		
	}
	
	
}
