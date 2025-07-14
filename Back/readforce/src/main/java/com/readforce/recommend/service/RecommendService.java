package com.readforce.recommend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.member.entity.Member;
import com.readforce.passage.service.CategoryService;
import com.readforce.passage.service.LevelService;
import com.readforce.passage.service.TypeService;
import com.readforce.question.dto.MultipleChoiceResponseDto;
import com.readforce.question.service.MultipleChoiceService;
import com.readforce.result.service.LearningService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendService {
	
	private final CategoryService categoryService;
	private final TypeService typeService;
	private final LevelService levelService;
	private final MultipleChoiceService multipleChoiceService;
	private final LearningService learningService;
	
	@Transactional(readOnly = true)
	public MultipleChoiceResponseDto getRecommendQuestion(Member member, LanguageEnum language) {

	    CategoryEnum weakCategory = categoryService.findWeakCategory(member, language);
	    
	    TypeEnum weakType = typeService.findWeakType(member, language, weakCategory);
	    
	    Integer optimalLevel = levelService.findOptimalLevel(member, language, weakCategory, weakType);

	    List<Long> solvedQuestionNos = learningService.getAllSolvedQuestionNos(member);

	    MultipleChoiceResponseDto recommendQuestion = multipleChoiceService.getUnsolvedMultipleChoiceQuestion(
	            member, language,weakCategory,weakType,optimalLevel,solvedQuestionNos
	    );

	    return recommendQuestion;
	    
	}





}
