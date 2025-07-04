package com.readforce.authentication.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RestController;

import com.readforce.authentication.service.AuthenticationService;
import com.readforce.authentication.util.JwtUtil;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.Name;
import com.readforce.member.dto.MemberSignInDto;
import com.readforce.member.dto.MemberSocialProviderDto;
import com.readforce.member.dto.MemberSummaryDto;
import com.readforce.member.service.AttendanceService;
import com.readforce.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
				Name.REFESH_TOKEN.name(), refreshToken,
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
	
}
