package com.readforce.result.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readforce.ranking.dto.RankingResponseDto;
import com.readforce.result.entity.Score;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

	@Query("""
			SELECT new com.readforce.ranking.dto.RankingResponseDto(m.nickname, m.email, s.score, c.category, l.language)
			FROM Score s
			JOIN s.member m
			JOIN s.category c
			JOIN s.language l
			WHERE c.category = :category AND l.language = :language
			ORDER BY s.score DESC			
			""")
	List<RankingResponseDto> findTop50ByCategory(
			@Param("category") String category,
			@Param("language") String language,
			Pageable pageable			
	);
	
}
