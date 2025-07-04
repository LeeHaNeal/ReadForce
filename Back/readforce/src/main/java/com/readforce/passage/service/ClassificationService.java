package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.ClassificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassificationService {
	
	private final ClassificationRepository classificationRepository;

}
