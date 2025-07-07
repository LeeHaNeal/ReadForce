package com.readforce.ranking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.Category;
import com.readforce.common.enums.Language;
import com.readforce.passage.validation.ValidEnum;
import com.readforce.ranking.dto.RankingResponseDto;
import com.readforce.result.service.ScoreService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
@Validated
public class RankingController {
	
	private final ScoreService scoreService;

	@GetMapping("/get-ranking-list")
	public ResponseEntity<List<RankingResponseDto>> getRankingList(
			@RequestParam("category")
			@NotBlank(message = MessageCode.CATEGORY_NOT_BLANK)
			@ValidEnum(enumClass = Category.class, message = MessageCode.CATEGORY_INVALID)
			String category,
			@RequestParam("language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language
	){
		
		List<RankingResponseDto> rankingList = scoreService.getTop50ByCategory(category, language);
		
		return ResponseEntity.status(HttpStatus.OK).body(rankingList);
		
	}
	
	
}
