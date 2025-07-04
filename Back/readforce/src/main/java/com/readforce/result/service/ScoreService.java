package com.readforce.result.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.member.entity.Member;
import com.readforce.result.entity.Score;
import com.readforce.result.repository.ScoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoreService {
	
	private final ScoreRepository scoreRepository;
	
	@Transactional
	public void createScore(Member member) {
		
		Score score = Score.builder()
				.member(member)
				.build();
		
		scoreRepository.save(score);
		
	}

}
