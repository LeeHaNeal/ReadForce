package com.readforce.adaptivelearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.adaptivelearning.entity.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategoryAndTypeAndLevel(String category, String type, Integer level);
}

