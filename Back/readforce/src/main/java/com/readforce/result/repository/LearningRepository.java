package com.readforce.result.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readforce.member.entity.Member;
import com.readforce.question.dto.QuestionSummaryResponseDto;
import com.readforce.result.entity.Learning;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

	List<Learning> findAllByMember(Member member);
	
	int countByMember_EmailAndCreatedAtBetween(String email, LocalDateTime start, LocalDateTime end);

	
	@Query("""
			SELECT com.readforce.question.dto.QuestionSummaryResponseDto(
				q.questionNo,
				p.title,
				l.createdAt,
				l.isCorrect
			)
			FROM Learning l
			JOIN l.question q
			JOIN q.passage p
			WHERE l.member.email = :email
			ORDER BY l.createdAt DESC			
			""")
	List<QuestionSummaryResponseDto> findAllByMember_Email(
			@Param("email") String email
	);

	
	@Query("""
			SELECT com.readforce.question.dto.QuestionSummaryResponseDto(
				q.questionNo,
				p.title,
				l.createdAt,
				l.isCorrect
			)
			FROM Learning l
			JOIN l.question q
			JOIN q.passage p
			WHERE l.member.email = :email
			AND l.isCorrect = false
			ORDER BY l.createdAt DESC			
			""")
	List<QuestionSummaryResponseDto> findIncorrectLearningByMember_Email(String email);

	
	@Query("""
			SELECT com.readforce.question.dto.QuestionSummaryResponseDto(
				q.questionNo,
				p.title,
				l.createdAt,
				l.isCorrect
			)
			FROM Learning l
			JOIN l.question q
			JOIN q.passage p
			WHERE l.member.email = :email
			AND l.createdAt BETWEEN :startOfDay AND :endOfDay
			ORDER BY l.createdAt DESC			
			""")
	List<QuestionSummaryResponseDto> findTodayLearningByMember_Email(
			@Param("email") String email, 
			@Param("startOfDay") LocalDateTime startOfDay,
			@Param("endOfDay") LocalDateTime endOfDay
	);
	
	@Query("""
			SELECT com.readforce.question.dto.QuestionSummaryResponseDto(
				q.questionNo,
				p.title,
				l.createdAt,
				l.isCorrect
			)
			FROM Learning l
			JOIN l.question q
			JOIN q.passage p
			WHERE l.member.email = :email
			AND l.createdAt BETWEEN :startOfDay AND :endOfDay
			AND l.isCorrect = false
			ORDER BY l.createdAt DESC			
			""")
	List<QuestionSummaryResponseDto> findTodayIncorrectLearningByMember_Email(
			@Param("email") String email, 
			@Param("startOfDay") LocalDateTime startOfDay,
			@Param("endOfDay") LocalDateTime endOfDay
	);

	@Query("""
			SELECT com.readforce.question.dto.QuestionSummaryResponseDto(
				q.questionNo,
				p.title,
				l.createdAt,
				l.isCorrect
			)
			FROM Learning l
			JOIN l.question q
			JOIN q.passage p
			WHERE l.member.email = :email
			AND isFavorit = true
			ORDER BY l.createdAt DESC			
			""")
	List<QuestionSummaryResponseDto> findFavoritLearningByMember_Email(
			@Param("email") String email
	);
	
}
