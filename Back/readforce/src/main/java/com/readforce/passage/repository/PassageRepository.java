package com.readforce.passage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.passage.entity.Passage;

@Repository
public interface PassageRepository extends JpaRepository<Passage, Long> {

}
