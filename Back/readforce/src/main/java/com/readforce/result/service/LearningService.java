package com.readforce.result.service;

import org.springframework.stereotype.Service;

import com.readforce.result.repository.LearningRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LearningService {
	
	private final LearningRepository learningRepository;

}
