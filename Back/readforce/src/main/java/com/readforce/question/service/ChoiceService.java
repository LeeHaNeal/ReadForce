package com.readforce.question.service;

import org.springframework.stereotype.Service;

import com.readforce.question.repository.ChoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChoiceService {

	private final ChoiceRepository choiceRepository;
	
}
