package com.readforce.result.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readforce.member.entity.Member;
import com.readforce.result.entity.Learning;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

	List<Learning> findAllByMember(Member member);
	
	int countByMember_EmailAndCreatedAtBetween(String email, LocalDateTime start, LocalDateTime end);

	
	@Query("""
			SELECT l
			FROM Learning l
			JOIN FETCH l.question q
			JOIN FETCH q.passage p
			WHERE l.member.email = :email
			ORDER BY l.createdAt DESC			
	""")
	List<Learning> findAllByMember_Email(
			@Param("email") String email
	);

	@Query("""
			SELECT l
			FROM Learning l
			JOIN FETCH l.question q
			JOIN FETCH q.passage p
			WHERE l.member.email = :email
			AND l.isCorrect = false
			ORDER BY l.createdAt DESC			
	""")
	List<Learning> findIncorrectLearningByMember_Email(String email);

	
	@Query("""
			SELECT l
			FROM Learning l
			JOIN FETCH l.question q
			JOIN FETCH q.passage p
			WHERE l.member.email = :email
			AND l.createdAt BETWEEN :startOfDay AND :endOfDay
			ORDER BY l.createdAt DESC	
	""")
	List<Learning> findTodayLearningByMember_Email(
			@Param("email") String email, 
			@Param("startOfDay") LocalDateTime startOfDay,
			@Param("endOfDay") LocalDateTime endOfDay
	);
	
	@Query("""
			SELECT l
			FROM Learning l
			JOIN FETCH l.question q
			JOIN FETCH q.passage p
			WHERE l.member.email = :email
			AND l.createdAt BETWEEN :startOfDay AND :endOfDay
			AND l.isCorrect = false
			ORDER BY l.createdAt DESC	
	""")
	List<Learning> findTodayIncorrectLearningByMember_Email(
			@Param("email") String email, 
			@Param("startOfDay") LocalDateTime startOfDay,
			@Param("endOfDay") LocalDateTime endOfDay
	);

	@Query("""
			SELECT l
			FROM Learning l
			JOIN FETCH l.question q
			JOIN FETCH q.passage p
			WHERE l.member.email = :email
			AND l.isFavorit = true
			ORDER BY l.createdAt DESC		
	""")
	List<Learning> findFavoritLearningByMember_Email(
			@Param("email") String email
	);
	
}