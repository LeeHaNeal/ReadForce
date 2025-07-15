package com.readforce.question.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.readforce.result.entity.Learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuestionSummaryResponseDto {

	private final Long questionNo;
	
	private final String title;
	
	private final LocalDateTime createdAt;
	
	@JsonProperty("isCorrect")
	private final boolean isCorrect;
	
	public QuestionSummaryResponseDto(Learning learning) {
		
		this.questionNo = learning.getQuestion().getQuestionNo();
		this.title = learning.getQuestion().getPassage().getTitle();
		this.createdAt = learning.getCreatedAt();
		this.isCorrect = learning.getIsCorrect();
		
	}
	
}
