package com.readforce.authentication.service;

import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.readforce.common.enums.Prefix;
import com.readforce.common.enums.Status;
import com.readforce.member.dto.MemberKeyInformationDto;
import com.readforce.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {
	
	private final MemberService memberService;
	private final StringRedisTemplate redisTemplate;

	@Value("${spring.jwt.refresh-expiration-time}")
	private long refreshExpirationTime;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		MemberKeyInformationDto memberKeyInformationDto = memberService.getMemberKeyInformationByEmailAndStatus(username, Status.ACTIVE);
				
		return new User(
				memberKeyInformationDto.getEmail(), 
				memberKeyInformationDto.getPassword(), 
				Collections.singletonList(new SimpleGrantedAuthority(Prefix.ROLE.getContent() + memberKeyInformationDto.getRole().name()))
		);
		
	}
	
	public void storeRefreshToken(String email, String refreshToken) {
		
		redisTemplate.opsForValue().set(
				Prefix.REFRESH.getContent() + email,
				refreshToken,
				Duration.ofMinutes(refreshExpirationTime)				
		);
		
	}
	
	public String getRefreshToken(String email) {
		
		return redisTemplate.opsForValue().get(Prefix.REFRESH.getContent() + email);
		
	}
	
	public void deleteRefreshToken(String email) {
		
		redisTemplate.delete(Prefix.REFRESH.getContent() + email);
		
	}

	
	
}
