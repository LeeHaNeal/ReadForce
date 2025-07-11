package com.readforce.question.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.Member;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.PassageService;
import com.readforce.question.dto.ChoiceDto;
import com.readforce.question.dto.MultipleChoiceResponseDto;
import com.readforce.question.dto.QuestionCheckResultDto;
import com.readforce.question.entity.Choice;
import com.readforce.question.entity.MultipleChoice;
import com.readforce.question.repository.MultipleChoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MultipleChoiceService {
	
	private final MultipleChoiceRepository multipleChoiceRepository;
	private final PassageService passageService;
	
	@Transactional(readOnly = true)
	public List<MultipleChoiceResponseDto> getMultipleChoiceQuestionListByPassageNo(Long passageNo){
		
		List<MultipleChoice> questionList = multipleChoiceRepository.findByPassage_PassageNo(passageNo);
		
		if(questionList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND);
			
		}
		
		return questionList.stream()
				.map(MultipleChoiceResponseDto::new)
				.collect(Collectors.toList());
		
	}

	@Transactional(readOnly = true)
	public QuestionCheckResultDto checkResult(Long questionNo, Integer selectedIndex) {

		MultipleChoice multipleChoice = multipleChoiceRepository.findById(questionNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND));
		
		boolean isCorrect = multipleChoice.getChoiceList().stream()
				.filter(choice -> choice.getChoiceIndex().equals(selectedIndex))
				.anyMatch(Choice::getIsCorrect);
		
		return new QuestionCheckResultDto(isCorrect, multipleChoice);

	}

	@Transactional(readOnly = true)
	public MultipleChoiceResponseDto getUnsolvedMultipleChoiceQuestion(Member member, LanguageEnum language, CategoryEnum category, TypeEnum type, Integer level, List<Long> solvedPassageNoList) {
		
		List<Long> passageNoList = passageService
				.getPassageNoListByLanguageAndClassificationAndCategoryAndTypeAndLevel(language, ClassificationEnum.NORMAL, category, type, level);

		List<Long> unsolvedPassageList = passageNoList.stream()
				.filter(passageNo -> !solvedPassageNoList.contains(passageNo))
				.collect(Collectors.toList());
		
		if(unsolvedPassageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		Collections.shuffle(unsolvedPassageList);
		
		Passage recommendPassage = passageService.getPassageByPassageNo(unsolvedPassageList.get(0));
		
		List<MultipleChoice> recommendMultipleChoice = multipleChoiceRepository.findByPassage_PassageNo(unsolvedPassageList.get(0)); 
		
		if(recommendMultipleChoice.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND);
			
		}
		
		return MultipleChoiceResponseDto.builder()
				.passageNo(recommendPassage.getPassageNo())
				.title(recommendPassage.getTitle())
				.content(recommendPassage.getContent())
				.author(recommendPassage.getAuthor())
				.publicationDate(recommendPassage.getPublicationDate())
				.category(recommendPassage.getCategory().getCategoryName().name())
				.level(recommendPassage.getLevel().getLevelNumber())
				.questionNo(recommendMultipleChoice.get(0).getQuestionNo())
				.question(recommendMultipleChoice.get(0).getQuestion())
				.choiceList(recommendMultipleChoice.get(0).getChoiceList().stream()
						.map(ChoiceDto::new)
						.collect(Collectors.toList())
				)
				.build();
		
	}

	@Transactional
	public void saveMultipleChoice(MultipleChoice multipleChoice) {

		multipleChoiceRepository.save(multipleChoice);
		
	}

	@Transactional(readOnly = true)
	public MultipleChoice getMultipleChoiceByQuestionNo(Long questionNo) {

		return multipleChoiceRepository.findById(questionNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.MULTIPLE_CHOICE_NOT_FOUND));

	}

}
