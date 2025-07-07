package com.readforce.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponseDto {

	private final String nickname;
	private final String email;
	private final Double score;
	private final String category;
	private final String language;
		
}
