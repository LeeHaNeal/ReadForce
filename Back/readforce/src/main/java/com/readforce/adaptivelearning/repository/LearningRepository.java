package com.readforce.adaptivelearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.readforce.adaptivelearning.entity.Learning;

import java.util.Set;

public interface LearningRepository extends JpaRepository<Learning, Long> {

    @Query("SELECT l.questionNo FROM Learning l WHERE l.memberNo = :memberNo")
    Set<Long> findSolvedQuestionNosByMemberNo(@Param("memberNo") Long memberNo);
}
