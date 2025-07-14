package com.readforce.question.dto;

import java.time.LocalDate;
import java.util.List;

import com.readforce.question.entity.Choice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChoiceDto {
	private final Long choiceNo;   
	
	private final Integer choiceIndex;
	
	private final String content;
	
	private final Boolean isCorrect;
	
	private final String explanation;
	
	public ChoiceDto(Choice choice) {
		this.choiceNo = choice.getChoiceNo();    
		this.choiceIndex = choice.getChoiceIndex();
		this.content = choice.getContent();
		this.isCorrect = choice.getIsCorrect();
		this.explanation = choice.getExplanation();
		
	}
	
}
