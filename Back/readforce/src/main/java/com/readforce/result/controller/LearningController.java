package com.readforce.result.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.result.dto.LearningMultipleChoiceRequestDto;
import com.readforce.result.service.LearningService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {
	
	private final LearningService learningService;
	
	@PostMapping("/save-multiple-choice")
	public ResponseEntity<Map<String, String>> saveMultipleChoice(
			@RequestBody LearningMultipleChoiceRequestDto learningMultipleChoiceRequestDto,
			@AuthenticationPrincipal UserDetails userDetails
	){
		
		String email = userDetails.getUsername();
		
		learningService.saveMuiltipleChoice(email, learningMultipleChoiceRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.SAVE_MULTIPLE_CHOICE_RESULT_SUCCESS
		));
		
	}

}
