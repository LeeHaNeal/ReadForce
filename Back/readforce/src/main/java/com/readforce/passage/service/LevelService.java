package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.LevelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {
	
	private final LevelRepository levelRepository;

}
