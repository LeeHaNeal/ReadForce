package com.readforce.passage.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.readforce.common.enums.Classification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassageResponseDto {

	private Long passageNo;

	private String title;

	private String content;

	private String author;

	private LocalDate publicationDate;

	private LocalDateTime createdAt;

	private String category;

	private String type;

	private Integer level;
	
	private String language;

	private Classification classification;

}
