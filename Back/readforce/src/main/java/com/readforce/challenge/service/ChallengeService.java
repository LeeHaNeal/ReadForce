package com.readforce.challenge.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.challenge.dto.ChallengeSubmitResultRequestDto;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.Member;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.CategoryService;
import com.readforce.passage.service.ClassificationService;
import com.readforce.passage.service.LanguageService;
import com.readforce.passage.service.LevelService;
import com.readforce.passage.service.PassageService;
import com.readforce.question.dto.MultipleChoiceResponseDto;
import com.readforce.question.dto.QuestionCheckResultDto;
import com.readforce.question.dto.QuestionLevelAndCategoryAndLanguageDto;
import com.readforce.question.service.MultipleChoiceService;
import com.readforce.question.service.QuestionService;
import com.readforce.result.service.ScoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	
	private LevelService levelService;
	private PassageService passageService;
	private MultipleChoiceService multipleChoiceService;
	private QuestionService questionService;
	private ScoreService scoreService;
	private CategoryService categoryService;
	private LanguageService languageService;
	private ClassificationService classificationService;
	
	@Transactional
	public List<MultipleChoiceResponseDto> getChallengeQuestionList(LanguageEnum language, CategoryEnum category) {
		
		List<MultipleChoiceResponseDto> resultList = new ArrayList<>();
		
		List<Level> allLevelList = levelService.getAllLevelList();
		
		for(Level level : allLevelList) {
			
			List<Passage> randomPassageList = passageService.getChallengePassageList(language, category, level.getLevelNumber());
			
			for(Passage passage : randomPassageList) {
				
				List<MultipleChoiceResponseDto> multipleChoiceDtoList = multipleChoiceService.getMultipleChoiceQuestionListByPassageNo(passage.getPassageNo());

				if(!multipleChoiceDtoList.isEmpty()) {
					
					MultipleChoiceResponseDto multipleChoiceDto = multipleChoiceDtoList.get(0);
					
					resultList.add(multipleChoiceDto);
					
				}
				
			}
			
		}
		
		if(resultList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND);
			
		}
	
		return resultList;
		
	}

	@Transactional
	public Double submitChallengeResult(Member member, ChallengeSubmitResultRequestDto requestDto) {

		double totalScore = 0.0;
		
		final long MAX_TIME_SECONDS = 1800;
		
		for(Map<Long, Integer> resultMap : requestDto.getSelecetedIndexList()) {
			
			for(Map.Entry<Long, Integer> entry : resultMap.entrySet()) {
				
				Long questionNo = entry.getKey();
				Integer selectedIndex = entry.getValue();
				
				QuestionCheckResultDto checkResult = multipleChoiceService.checkResult(questionNo, selectedIndex);
				
				boolean isCorrect = checkResult.getIsCorrect();
				
				if(isCorrect) {
					
					QuestionLevelAndCategoryAndLanguageDto questionInfo = questionService.getQuestionLevelAndCategoryAndLanguage(questionNo);

					Level level = levelService.getLevelByLevel(questionInfo.getLevel());
					
					double baseScore = level.getLevelNumber() * 4;
					
					totalScore += baseScore;
					
				}
				
			}
			
		}
		
		long solvingTime = requestDto.getTotalQuestionSolvingTime();
		if(solvingTime < MAX_TIME_SECONDS) {
			
			double timeBonus = (1 - (double) solvingTime / MAX_TIME_SECONDS) * 50;
			
			totalScore += timeBonus;
			
		}
		
		Category category = categoryService.getCategoryByCategory(requestDto.getCategory());
		
		Language language = languageService.getLangeageByLanguage(requestDto.getLanguage());
		
		scoreService.createScore(
				member,
				totalScore,
				category,
				language
		);
		
		return totalScore;
		
	}

	@Transactional
	public void updateToChallengePassages() {
		
		List<Language> languageList = languageService.getAllLanguageList();
		
		List<Category> categoryList = categoryService.getAllCategoryList();
		
		List<Level> levelList = levelService.getAllLevelList();
		
		Classification ChallengeClassification = classificationService.getClassificationByClassfication(ClassificationEnum.CHALLENGE);
		
		for(Language language : languageList) {
			
			for(Category category : categoryList) {
				
				for(Level level : levelList) {
					
					List<Passage> passageList = new ArrayList<>(passageService.getNormalPassages(
							language.getLanguageName(), 
							category.getCategoryName(), 
							level.getLevelNumber()							
					));
					
					Collections.shuffle(passageList);
					
					passageList
						.stream()
						.limit(2)
						.forEach(passage -> {
							passage.changeClassification(ChallengeClassification);							
						});

				}
				
			}
			
		}
		
	}
	
	@Transactional
	public void resetWeeklyChallenge() {
		
		revertExistingChallengesToNormal();
		
		updateToChallengePassages();		
		
	}
	
	private void revertExistingChallengesToNormal() {
		
		List<Passage> existingChallenges = passageService.getAllPassagesByClassification(ClassificationEnum.CHALLENGE);
		
		if(!existingChallenges.isEmpty()) {
			
			Classification normalClassification = classificationService.getClassificationByClassfication(ClassificationEnum.NORMAL);
			
			existingChallenges.forEach(passage -> passage.changeClassification(normalClassification));
			
		}
		
	}

}
