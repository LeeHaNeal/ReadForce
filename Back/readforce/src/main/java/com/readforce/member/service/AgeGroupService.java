package com.readforce.member.service;

import org.springframework.stereotype.Service;

import com.readforce.member.repository.AgeGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgeGroupService {

	private final AgeGroupRepository ageGroupRepository;
	
}
