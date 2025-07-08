package com.readforce.result.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.member.entity.Member;
import com.readforce.ranking.dto.RankingResponseDto;
import com.readforce.result.entity.Score;
import com.readforce.result.repository.ScoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoreService {
	
	private final ScoreRepository scoreRepository;
	
	@Transactional
	public void createScore(Member member) {
		
		Score score = Score.builder()
				.member(member)
				.build();
		
		scoreRepository.save(score);
		
	}
	
	@Transactional(readOnly = true)
	public List<RankingResponseDto> getTop50ByCategory(CategoryEnum category, LanguageEnum language){
		
		Pageable pageable = PageRequest.of(0, 50);
		
		List<Score> scoreList = scoreRepository.findTopScoreListByCategoryAndLanguage(category, language, pageable);

		return scoreList.stream()
				.map(RankingResponseDto::new)
				.collect(Collectors.toList());
		
	}

}
