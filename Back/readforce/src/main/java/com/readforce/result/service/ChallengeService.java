package com.readforce.result.service;

import org.springframework.stereotype.Service;

import com.readforce.result.repository.ChallengeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	
	private final ChallengeRepository challengeRepository;

}
