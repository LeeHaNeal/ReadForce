package com.readforce.result.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Type;
import com.readforce.result.entity.Result;
import com.readforce.result.entity.ResultMetric;
import com.readforce.result.repository.ResultMetricRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultMetricService {
	
	private final ResultMetricRepository resultMetricRepository;

	@Transactional(readOnly = true)
	public void createResultMetric(Result result, Language language, Category category, Type type, Level level) {

		ResultMetric resultMetric = ResultMetric.builder()
				.result(result)
				.language(language)
				.category(category)
				.type(type)
				.level(level)
				.build();
		
		resultMetricRepository.save(resultMetric);	
		
	}

	@Transactional(readOnly = true)
	public List<ResultMetric> getAllByResultAndLanguage_Language(Result result, String language) {

		return resultMetricRepository.findAllByResultAndLanguage_Language(result, language);

	}

	@Transactional(readOnly = true)
	public List<ResultMetric> getAllByResult(Result result) {
		
		List<ResultMetric> resultList = resultMetricRepository.findAllByResult(result);
		
		if(resultList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.RESULT_METRIC_NOT_FOUND);
			
		}
		
		return resultList;

	}

	@Transactional(readOnly = true)
	public Map<String, Double> getCategoryCorrectAnswerRate(Result result) {
		
		List<ResultMetric> resultMetricList = resultMetricRepository.findAllByResult(result);
		
		if(resultMetricList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.RESULT_METRIC_NOT_FOUND);
			
		}
		
		return resultMetricList.stream()
				.filter(metric -> metric.getCategory() != null && metric.getType() == null && metric.getLevel() == null)
				.collect(Collectors.toMap(
						metric -> metric.getCategory().getCategory(),
						ResultMetric::getCorrectAnswerRate,
						(rate1, rate2) -> rate2
				));

	}

	@Transactional(readOnly = true)
	public Map<String, Double> getTypeCorrectAnswerRate(Result result, String category) {

		List<ResultMetric> resultMetricList = resultMetricRepository.findAllByResult(result);
		
		if(resultMetricList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.RESULT_METRIC_NOT_FOUND);
			
		}
		
		return resultMetricList.stream()
				.filter(metric -> metric.getCategory() != null && metric.getCategory().getCategory().equals(category))
				.filter(metric -> metric.getType() == null && metric.getLevel() == null)
				.collect(Collectors.toMap(
						metric -> metric.getType().getType(),
						ResultMetric::getCorrectAnswerRate,
						(rate1, rate2) -> rate2
				));
		
	}

}
