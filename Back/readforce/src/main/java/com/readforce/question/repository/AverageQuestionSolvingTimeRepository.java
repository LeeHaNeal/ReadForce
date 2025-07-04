package com.readforce.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.question.entity.AverageQuestionSolvingTime;

@Repository
public interface AverageQuestionSolvingTimeRepository extends JpaRepository<AverageQuestionSolvingTime, Long> {

}
