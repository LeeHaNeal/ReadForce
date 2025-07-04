package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.LanguageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LanguageService {
	
	private final LanguageRepository languageRepository;

}
