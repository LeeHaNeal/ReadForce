package com.readforce.result.service;

import org.springframework.stereotype.Service;

import com.readforce.member.entity.Member;
import com.readforce.result.entity.Result;
import com.readforce.result.repository.ResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultService {
	
	private final ResultRepository resultRepository;

	public void create(Member member) {
		
		Result result = Result.builder()
				.member(member)
				.build();
		
		resultRepository.save(result);
		
	}

}
