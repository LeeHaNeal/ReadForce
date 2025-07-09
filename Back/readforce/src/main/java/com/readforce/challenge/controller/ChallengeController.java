package com.readforce.challenge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.challenge.service.ChallengeService;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.question.dto.MultipleChoiceResponseDto;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
@Validated
public class ChallengeController {
	
	private final ChallengeService challengeService;

	@GetMapping("/get-challenge-question-list")
	public ResponseEntity<List<MultipleChoiceResponseDto>> getChallengeQuestionList(
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language,
			@RequestParam("category")
			@NotNull(message = MessageCode.CATEGORY_NOT_NULL)
			CategoryEnum category
	){
		
		List<MultipleChoiceResponseDto> resultList = challengeService.getChallengeQuestionList(language, category);
		
		return ResponseEntity.status(HttpStatus.OK).body(resultList);
		
	}
//	
//	@PostMapping("/submit-challenge-result")
//	public ResponseEntity<Map<String, String>> submitChallengeResult(
//			@RequestBody ChallengeSubmitResultRequestDto requestDto
//	){
//		
//		
//		
//	}
	
}
