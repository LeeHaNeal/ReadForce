package com.readforce.ai.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.ai.service.AiService;
import com.readforce.common.MessageCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Validated
public class AiController {

	private final AiService aiService;
	
	@PostMapping("/generate-test")
	public ResponseEntity<Map<String, String>> generateTest(
		@RequestBody AiGenerateTestRequestDto aiGenerateTestRequestDto
	){
		
		aiService.generateTestVocabulary(aiGenerateTestRequestDto.getLanguage());
		
		aiService.generateTestQuestion(aiGenerateTestRequestDto.getLanguage());
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.GENERATE_TEST_SUCCESS
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
	
}
