package com.readforce.question.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.member.entity.AgeGroup;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Type;
import com.readforce.question.entity.AverageQuestionSolvingTime;
import com.readforce.question.repository.AverageQuestionSolvingTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AverageQuestionSolvingTimeService {

	private final AverageQuestionSolvingTimeRepository averageQuestionSolvingTimeRepository;
	
	@Transactional(readOnly = true)
	public Optional<Long> getAverageTime(AgeGroup ageGroup, Category category, Type type, Level level, Language language){
		
		return averageQuestionSolvingTimeRepository
				.findByAgeGroupAndCategoryAndTypeAndLevelAndLanguage(ageGroup, category, type, level, language)
				.map(AverageQuestionSolvingTime::getAverageQuestionSolvingTime);				
		
	}
	
}
