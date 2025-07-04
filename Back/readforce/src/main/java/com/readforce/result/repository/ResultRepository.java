package com.readforce.result.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.Result;
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

}
