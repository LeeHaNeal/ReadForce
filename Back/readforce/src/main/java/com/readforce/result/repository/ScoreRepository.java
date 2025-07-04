package com.readforce.result.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.Score;
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

}
