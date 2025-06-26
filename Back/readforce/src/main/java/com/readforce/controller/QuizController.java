package com.readforce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.dto.QuizDto.GetQuiz;
import com.readforce.service.QuizService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Validated
@Slf4j
public class QuizController {

	private final QuizService quiz_service;
	
	// 가장 많이 틀린 문제 가져오기(5개)
	@GetMapping("/get-most-incorrected-quiz")
	public ResponseEntity<List<GetQuiz>> getMostIncorrectedQuiz(){
		
		List<GetQuiz> get_quiz_list = quiz_service.getMostIncorrectedQuiz();
		
		return ResponseEntity.status(HttpStatus.OK).body(get_quiz_list);
	
	}
	
	// 사용자 전체 문제 정답률 구하기
	@GetMapping("/get-correct-rate")
	public ResponseEntity<Integer> getCorrectRate(@AuthenticationPrincipal UserDetails userDetails) {
	    String email = userDetails.getUsername();
	    int rate = quiz_service.calculateUserCorrectRate(email);
	    return ResponseEntity.ok(rate);
	}
	
}
