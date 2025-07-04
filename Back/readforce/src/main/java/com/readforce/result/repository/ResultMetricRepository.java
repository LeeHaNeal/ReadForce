package com.readforce.result.repository;

import com.readforce.result.entity.Result;
import com.readforce.result.entity.ResultMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultMetricRepository extends JpaRepository<ResultMetric, Long> {

   
    List<ResultMetric> findByResult(Result result);
}

