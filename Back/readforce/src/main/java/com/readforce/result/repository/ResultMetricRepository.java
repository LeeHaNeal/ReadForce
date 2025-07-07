package com.readforce.result.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.result.entity.Result;
import com.readforce.result.entity.ResultMetric;

@Repository
public interface ResultMetricRepository extends JpaRepository<ResultMetric, Long> {

	List<ResultMetric> findAllByResultAndLanguage_Language(Result result, String language);

	List<ResultMetric> findAllByResult(Result result);
	
}