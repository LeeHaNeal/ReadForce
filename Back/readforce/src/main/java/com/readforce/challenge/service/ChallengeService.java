package com.readforce.challenge.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.LevelService;
import com.readforce.passage.service.PassageService;
import com.readforce.question.dto.MultipleChoiceResponseDto;
import com.readforce.question.service.MultipleChoiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	
	private LevelService levelService;
	private PassageService passageService;
	private MultipleChoiceService multipleChoiceService;
	
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

}
