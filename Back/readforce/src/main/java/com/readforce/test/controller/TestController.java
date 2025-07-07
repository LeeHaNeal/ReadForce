package com.readforce.test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.Category;
import com.readforce.common.enums.Language;
import com.readforce.passage.validation.ValidEnum;
import com.readforce.question.dto.QuestionTestResponseDto;
import com.readforce.question.dto.QuestionTestResultDto;
import com.readforce.test.dto.TestSubmitRequestDto;
import com.readforce.test.service.TestService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Validated
public class TestController {

	private TestService testService;
	
	@GetMapping("/start")
	public ResponseEntity<QuestionTestResponseDto> start(
			@RequestParam("language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language
	){
		
		QuestionTestResponseDto startQuestion = testService.getTestQuestion(language, Category.VOCABULARY.name(), 6);
		
		return ResponseEntity.status(HttpStatus.OK).body(startQuestion);
		
	}
	
	@PostMapping("/submit-vocabulary-result")
	public ResponseEntity<QuestionTestResponseDto> submitVocabularyResult(
			@RequestBody TestSubmitRequestDto submitRequestDto
	){
		
		QuestionTestResponseDto nextQuestion = testService.submitVocabularyResult(submitRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(nextQuestion);
		
	}
	
	@PostMapping("/submit-factual-result")
	public ResponseEntity<?> submitFactualResult(
			@RequestBody TestSubmitRequestDto submitRequestDto
	){
		
		Object result = testService.submitFactualResult(submitRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
		
	}
	
	
	@PostMapping("/submit-inferential-result")
	public ResponseEntity<QuestionTestResultDto> submitInferentialResult(
			@RequestBody TestSubmitRequestDto submitRequestDto
	){
		
		QuestionTestResultDto result = testService.submitInferentialResult(submitRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
		
	}

	
}
