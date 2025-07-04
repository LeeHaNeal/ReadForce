package com.readforce.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.question.entity.MultipleChoice;

@Repository
public interface MultipleChoiceRepository extends JpaRepository<MultipleChoice, Long> {

}
