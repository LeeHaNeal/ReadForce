package com.readforce.question.service;

import org.springframework.stereotype.Service;

import com.readforce.question.repository.MultipleChoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MultipleChoiceService {
	
	private final MultipleChoiceRepository multipleChoiceRepository;

}
