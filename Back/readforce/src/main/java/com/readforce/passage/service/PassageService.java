package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.PassageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
	
	private final PassageRepository passageRepository;

}
