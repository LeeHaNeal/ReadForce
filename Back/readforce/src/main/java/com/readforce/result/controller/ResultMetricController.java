package com.readforce.result.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.result.service.ResultMetricService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/result_metric")
@RequiredArgsConstructor
public class ResultMetricController {

	private final ResultMetricService resultMetricService;
	
	@GetMapping("/get-category-correct-answer-rate")
	public ResponseEntity
	
}
