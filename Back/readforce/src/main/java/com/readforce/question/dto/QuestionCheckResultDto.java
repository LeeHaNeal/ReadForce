package com.readforce.question.dto;

import com.readforce.question.entity.MultipleChoice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionCheckResultDto {
	
	private final Boolean isCorrect;
	private final MultipleChoice multipleChoice;

}
