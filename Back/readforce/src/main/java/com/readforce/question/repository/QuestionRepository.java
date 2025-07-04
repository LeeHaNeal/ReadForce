package com.readforce.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.question.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByPassage_Category_NameAndPassage_Type_NameAndPassage_Level_LevelNo(String categoryName, String typeName, Integer levelNo);

}
