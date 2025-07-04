package com.readforce.question.service;

import org.springframework.stereotype.Service;

import com.readforce.question.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;

}
