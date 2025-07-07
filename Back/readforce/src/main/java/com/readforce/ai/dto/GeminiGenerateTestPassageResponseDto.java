package com.readforce.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GeminiGenerateTestPassageResponseDto {

	private String title;
	
	private String content;
	
	private int level;
	
}
