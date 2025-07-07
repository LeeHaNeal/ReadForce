package com.readforce.question.dto;

import com.readforce.question.entity.Choice;

import lombok.Getter;

@Getter
public class ChoiceDto {
	
	private final Integer choiceIndex;
	
	private final String content;
	
	private final Boolean isCorrect;
	
	public ChoiceDto(Choice choice) {
		
		this.choiceIndex = choice.getChoiceIndex();
		this.content = choice.getContent();
		this.isCorrect = choice.getIsCorrect();
		
	}
	
}
