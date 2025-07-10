package com.readforce.administrator.controller.question;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.question.service.QuestionService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/administrator/question")
@RequiredArgsConstructor
@Validated
public class AdministratorQuestionController {

	private final QuestionService questionService;
	
	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> delete(
		@RequestParam("questionNo")
		@NotNull(message = MessageCode.QUESTION_NO_NOT_NULL)
		Long questionNo
	){
		
		//questionService.deleteQuestion(questionNo);
		
		
		return null;
	}
	
	
}
