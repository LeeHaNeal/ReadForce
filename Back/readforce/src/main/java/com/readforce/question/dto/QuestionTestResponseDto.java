package com.readforce.question.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuestionTestResponseDto {
	
	@Override
	public String toString() {
		return "QuestionTestResponseDto [testerId=" + testerId + ", passageNo=" + passageNo + ", title=" + title
				+ ", content=" + content + ", author=" + author + ", publicationDate=" + publicationDate + ", category="
				+ category + ", level=" + level + ", questionNo=" + questionNo + ", multipleChoiceNo="
				+ multipleChoiceNo + ", question=" + question + ", choiceList=" + choiceList + "]";
	}

	private final String testerId;

	private final Long passageNo;
	
	private final String title;

	private final String content;
	
	private final String author;
	
	private final LocalDate publicationDate;
	
	private final String category;
	
	private final Integer level;
	
	private final Long questionNo;
	
	private final Long multipleChoiceNo;
	
	private final String question;
	
	private final List<ChoiceDto> choiceList;	
	
}
