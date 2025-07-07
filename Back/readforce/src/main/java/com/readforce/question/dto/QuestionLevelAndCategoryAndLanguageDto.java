package com.readforce.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuestionLevelAndCategoryAndLanguageDto {

	private String category;
	
	private String language;
	
	private Integer level;
	
}
