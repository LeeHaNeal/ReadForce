package com.readforce.member.service;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.AgeGroup;
import com.readforce.member.entity.Member;
import com.readforce.member.repository.AgeGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgeGroupService {

	private final AgeGroupRepository ageGroupRepository;

	public AgeGroup getAgeGroupForMember(Member member) {
		
		int age = Period.between(member.getBirthday(), LocalDate.now()).getYears();
		
		int ageGroupValue = (age / 10) * 10;
		
		return ageGroupRepository.findByAgeGroup(ageGroupValue)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.AGE_GROUP_NOT_FOUND));
		
	}
	
}
