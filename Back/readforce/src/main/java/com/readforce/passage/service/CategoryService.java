package com.readforce.passage.service;

import org.springframework.stereotype.Service;

import com.readforce.passage.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
}
