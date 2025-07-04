package com.readforce.result.repository;

import com.readforce.result.entity.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

    @Query("select l.question.questionNo from Learning l where l.member.memberNo = :memberNo and l.isCorrect = true")
    Set<Long> findSolvedQuestionNosByMemberNo(Long memberNo);
}
