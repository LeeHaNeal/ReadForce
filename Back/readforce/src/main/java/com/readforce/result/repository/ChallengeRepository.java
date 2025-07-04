package com.readforce.result.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

}
