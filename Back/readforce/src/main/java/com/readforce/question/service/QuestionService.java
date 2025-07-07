package com.readforce.question.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.question.dto.QuestionLevelAndCategoryAndLanguageDto;
import com.readforce.question.entity.Question;
import com.readforce.question.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;

	@Transactional(readOnly = true)
	public Question getQuestion(Long questionNo) {

		return questionRepository.findById(questionNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND));

	}
	
	@Transactional(readOnly = true)
	private Question getQuestionWithPassage(Long questionNo) {
		
		return questionRepository.findByIdWithPassage(questionNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.QUESTION_NOT_FOUND));
		
	}

	@Transactional(readOnly = true)
	public QuestionLevelAndCategoryAndLanguageDto getQuestionLevelAndCategoryAndLanguage(Long questionNo) {

		Question question = getQuestionWithPassage(questionNo);
		
		return QuestionLevelAndCategoryAndLanguageDto.builder()
				.language(question.getPassage().getLanguage().getLanguage())
				.category(question.getPassage().getCategory().getCategory())
				.level(question.getPassage().getLevel().getLevel())
				.build();

	}
	
	



}
