package com.readforce.result.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.ResultMetric;
@Repository
public interface ResultMetricRepository extends JpaRepository<ResultMetric, Long> {

}
