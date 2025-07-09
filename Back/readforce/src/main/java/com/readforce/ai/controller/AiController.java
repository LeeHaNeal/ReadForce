package com.readforce.ai.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.ai.service.AiService;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.LanguageEnum;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Validated
public class AiController {

	private final AiService aiService;
	
	@PostMapping("/generate-test-passage")
	public ResponseEntity<Map<String, String>> generateTestPassage(
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language
	){
		
		aiService.generateTestVocabulary(language);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_TEST_PASSAGE_SUCCESS
		));
		
	}
	
	@PostMapping("/generate-test-question")
	public ResponseEntity<Map<String, String>> generateTestQuestion(
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language
	){
		
		aiService.generateTestQuestion(language);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_TEST_QUESTION_SUCCESS
		));
		
	}
	
	
	@PostMapping("/generate-passage")
	public ResponseEntity<Map<String, String>> generatePassage(
			@RequestBody AiGeneratePassageRequestDto aiGeneratePassageRequestDto
	){
		
		aiService.generatePassage(aiGeneratePassageRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_PASSAGE_SUCCESS
		));
		
	}
	
	
	@PostMapping("/generate-question")
	public ResponseEntity<Map<String, String>> generateQuestion(){
		
		aiService.generateQuestion();
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_QUESTION_SUCCESS
		));
		
	}
	
	@PostMapping("/generate-challenge-questions")
	public ResponseEntity<Map<String, String>> generateChallengeQuestions(){
		
		aiService.generateChallengeQuestions();
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_CHALLENGE_QUESTIONS_SUCCESS
		));
		
	}
	
	@PostMapping("/generate-challenge-passages")
	public ResponseEntity<Map<String, String>> generateChallengePassages(){
		
		aiService.generateChallengePassages();
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_CHALLENGE_PASSAGES_SUCCESS
		));
		
	}
	
}
