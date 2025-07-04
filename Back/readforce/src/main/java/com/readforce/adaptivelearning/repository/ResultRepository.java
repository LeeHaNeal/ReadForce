package com.readforce.adaptivelearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.adaptivelearning.entity.Result;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByMemberNo(Long memberNo);
}

