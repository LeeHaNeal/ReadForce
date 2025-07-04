package com.readforce.passage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.passage.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

}
