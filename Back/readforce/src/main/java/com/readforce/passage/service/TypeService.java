package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.TypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeService {
	
	private final TypeRepository typeRepository;

}
