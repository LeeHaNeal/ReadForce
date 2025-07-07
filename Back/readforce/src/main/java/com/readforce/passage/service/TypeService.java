package com.readforce.passage.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.Member;
import com.readforce.passage.entity.Type;
import com.readforce.passage.repository.TypeRepository;
import com.readforce.result.entity.Result;
import com.readforce.result.entity.ResultMetric;
import com.readforce.result.service.ResultMetricService;
import com.readforce.result.service.ResultService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeService {
	
	private final TypeRepository typeRepository;
	private final ResultService resultService;
	private final ResultMetricService resultMetricService;

	@Transactional(readOnly = true)
	public List<Type> getAllTypeList() {

		return typeRepository.findAll();
		
	}

	@Transactional(readOnly = true)
	public String findWeakType(Member member, String language, String weakCategory) {

		Result result = resultService.getActiveMemberResultByEmail(member.getEmail());
		
		List<ResultMetric> metricList = resultMetricService.getAllByResultAndLanguage_Language(result, language);
		
		for(double threshold = 0.55; threshold <= 1.0; threshold += 0.05) {
			
			final double currentThreshold = threshold;
			
			Optional<Type> weakType = metricList.stream()
					.filter(metric -> weakCategory.equals(metric.getCategory().getCategory()))
					.filter(metric -> metric.getType() != null
						&& metric.getCorrectAnswerRate() != null
						&& metric.getCorrectAnswerRate() <= currentThreshold)
					.min(Comparator.comparing(ResultMetric::getCorrectAnswerRate))
					.map(ResultMetric::getType);
			
			if(weakType.isPresent()) {
				
				return weakType.get().getType();
				
			}
			
		}
		
		throw new ResourceNotFoundException(MessageCode.WEAK_TYPE_NOT_FOUND);
		
	}

	@Transactional(readOnly = true)
	public Type getTypeByType(String type) {

		return typeRepository.findByType(type)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.TYPE_NOT_FOUND));

	}

}
