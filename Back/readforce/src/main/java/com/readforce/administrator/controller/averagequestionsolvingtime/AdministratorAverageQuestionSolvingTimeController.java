package com.readforce.administrator.controller.averagequestionsolvingtime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.administrator.dto.AdministratorAverageQuestionSolvingTimeRequestDto;
import com.readforce.administrator.dto.AdministratorAverageQuetionSolvingTimeResponseDto;
import com.readforce.common.MessageCode;
import com.readforce.question.service.AverageQuestionSolvingTimeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/administrator/average-question-solving-time")
@RequiredArgsConstructor
@Validated
public class AdministratorAverageQuestionSolvingTimeController {

	private final AverageQuestionSolvingTimeService averageQuestionSolvingTimeService;

	@GetMapping("/get-all-list")
	public ResponseEntity<List<AdministratorAverageQuetionSolvingTimeResponseDto>> getAllList(){
		
		List<AdministratorAverageQuetionSolvingTimeResponseDto> resultList = 
				averageQuestionSolvingTimeService.getAllAverageQuestionSolvingTimeList().stream()
				.map(AdministratorAverageQuetionSolvingTimeResponseDto::new)
				.collect(Collectors.toList());
		
		return ResponseEntity.status(HttpStatus.OK).body(resultList);
		
	}
	
	@PostMapping("/create")
	public ResponseEntity<Map<String, String>> create(
			@Valid @RequestBody AdministratorAverageQuestionSolvingTimeRequestDto requestDto
	){
		
		averageQuestionSolvingTimeService.createAverageQuestionSolvingTime(requestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.CREATE_AVERAGE_QUESTION_SOLVING_TIME_SUCCESS
		));
		
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> delete(
			@RequestParam("averageQuestionSolvingTimeNo")
			@NotNull(message = MessageCode.AVERAGE_QUESTION_SOLVING_TIME_NO_NOT_NULL)
			Long averageQuestionSolvingTimeNo
	){
		
		averageQuestionSolvingTimeService.deleteAverageQuestionSolvingTime(averageQuestionSolvingTimeNo);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.DELETE_AVERAGE_QUESTION_SOLVING_TIME_SUCCESS
		));
		
	}
	
}

