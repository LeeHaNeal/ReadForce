package com.readforce.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.question.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
