package com.readforce.result.service;

import org.springframework.stereotype.Service;

import com.readforce.result.repository.ResultMetricRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultMetricService {
	
	private final ResultMetricRepository resultMetricRepository;

}
