package com.readforce.result.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.Learning;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

}
