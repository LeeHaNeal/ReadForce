package com.readforce.question.service;

import org.springframework.stereotype.Service;

import com.readforce.question.repository.AverageQuestionSolvingTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AverageQuestionSolvingTimeService {

	private final AverageQuestionSolvingTimeRepository averageQuestionSolvingTimeRepository;
	
}
