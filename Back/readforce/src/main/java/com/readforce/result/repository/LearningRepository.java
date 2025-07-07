package com.readforce.result.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.member.entity.Member;
import com.readforce.result.entity.Learning;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

	List<Learning> findAllByMember(Member member);
	
	int countByMember_EmailAndCreatedAtBetween(String email, LocalDateTime start, LocalDateTime end);
	
}
