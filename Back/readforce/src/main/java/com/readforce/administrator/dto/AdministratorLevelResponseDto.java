package com.readforce.administrator.dto;

import java.time.LocalDateTime;

import com.readforce.passage.entity.Level;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorLevelResponseDto {

	private Long levelNo;
	
	private Integer level;
	
	private Integer paragraphCount;
	
	private String sentenceStructure;
	
	private String questionType;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime lastModifiedAt;
	
	public AdministratorLevelResponseDto(Level level) {
		
		this.levelNo = level.getLevelNo();
		this.level = level.getLevelNumber();
		this.paragraphCount = level.getParagraphCount();
		this.sentenceStructure = level.getSentenceStructure();
		this.questionType = level.getQuestionType();
		this.createdAt = level.getCreatedAt();
		this.lastModifiedAt = level.getLastModifiedAt();
		
	}
	
}
