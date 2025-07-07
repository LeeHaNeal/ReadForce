package com.readforce.recommend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.Language;
import com.readforce.member.entity.Member;
import com.readforce.member.service.MemberService;
import com.readforce.passage.validation.ValidEnum;
import com.readforce.question.dto.MultipleChoiceResponseDto;
import com.readforce.recommend.service.RecommendService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@Validated
public class RecommendController {

	private final RecommendService recommendService;
	private final MemberService memberService;
	
	@GetMapping("/get-recommend")
	public ResponseEntity<MultipleChoiceResponseDto> getRecommend(
			@RequestParam("/language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language,
			@AuthenticationPrincipal UserDetails userDetails
	){
		
		String email = userDetails.getUsername();
		
		Member member = memberService.getActiveMemberByEmail(email);
		
		MultipleChoiceResponseDto recommendQuestion = recommendService.getRecommendQuestion(member, language);
		
		return ResponseEntity.status(HttpStatus.OK).body(recommendQuestion);
		
	}
	
}
