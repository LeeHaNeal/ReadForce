package com.readforce.authentication.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.readforce.authentication.exception.AuthenticationException;
import com.readforce.authentication.exception.JwtException;
import com.readforce.authentication.service.AuthenticationService;
import com.readforce.authentication.util.JwtUtil;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.Header;
import com.readforce.common.enums.Prefix;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private final AuthenticationService authenticationService;
	private final JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

		final String authorizationHeader = httpServletRequest.getHeader(Header.AUTHORIZATION.getContent());
		
		String username = null;
		String accessToken = null;
		
		if(authorizationHeader != null && authorizationHeader.startsWith(Prefix.BEARER.getContent())) {
			
			accessToken = authorizationHeader.substring(Prefix.BEARER.getContent().length());
			
			try {
				
				username = jwtUtil.extractUsername(accessToken);
				
			} catch(ExpiredJwtException exception) {
				
				log.warn("요청된 JWT 토큰이 만료되었습니다: {}", exception.getMessage());
				
				throw new JwtException(MessageCode.ACCESS_TOKEN_EXPIRED);
				
			} catch(Exception exception) {
				
				log.error("JWT 토큰 파싱 중 오류 발생: {}", exception.getMessage());
				
			}

		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			UserDetails userDetails = this.authenticationService.loadUserByUsername(username);
			
			if(jwtUtil.isExpiredToken(accessToken)) {
				
				throw new AuthenticationException(MessageCode.ACCESS_TOKEN_EXPIRED);
				
			}
			
			if(jwtUtil.validateToken(accessToken, userDetails)) {
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				
			} else {
				
				throw new AuthenticationException(MessageCode.AUTHENTICATION_FAIL);
				
			}
			
		}
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	
	}
	
}
