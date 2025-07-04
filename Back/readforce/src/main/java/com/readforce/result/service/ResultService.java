package com.readforce.result.service;

import org.springframework.stereotype.Service;

import com.readforce.result.repository.ResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultService {
	
	private final ResultRepository resultRepository;

}
