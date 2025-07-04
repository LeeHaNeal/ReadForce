package com.readforce.result.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.result.entity.Result;
import com.readforce.result.entity.ResultMetric;
import com.readforce.result.repository.ResultMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultMetricService {

    private final ResultMetricRepository resultMetricRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 카테고리별 정답률 JSON 반환
     */
    public String getCategoryAccuracyJson(Result result) {
        List<ResultMetric> metrics = resultMetricRepository.findByResult(result);
        Map<String, Double> categoryAccuracy = new HashMap<>();

        for (ResultMetric metric : metrics) {
            if (metric.getCategory() != null) {
                categoryAccuracy.put(
                        metric.getCategory().getName(),  // ✅ getName() 으로 수정
                        metric.getCorrectAnswerRate()
                );
            }
        }

        return toJson(categoryAccuracy);
    }

    /**
     * 타입별 정답률 JSON 반환
     */
    public String getTypeAccuracyJson(Result result) {
        List<ResultMetric> metrics = resultMetricRepository.findByResult(result);
        Map<String, Double> typeAccuracy = new HashMap<>();

        for (ResultMetric metric : metrics) {
            if (metric.getType() != null) {
                typeAccuracy.put(
                        metric.getType().getName(),  // ✅ getName() 으로 수정
                        metric.getCorrectAnswerRate()
                );
            }
        }

        return toJson(typeAccuracy);
    }

    /**
     * 레벨별 정답률 JSON 반환
     */
    public String getLevelAccuracyJson(Result result) {
        List<ResultMetric> metrics = resultMetricRepository.findByResult(result);
        Map<String, Double> levelAccuracy = new HashMap<>();

        for (ResultMetric metric : metrics) {
            if (metric.getLevel() != null) {
                levelAccuracy.put(
                        String.valueOf(metric.getLevel().getLevelNo()),  // ✅ getLevelNo()
                        metric.getCorrectAnswerRate()
                );
            }
        }

        return toJson(levelAccuracy);
    }

    /**
     * 레벨별 평균 풀이 시간 JSON 반환
     */
    public String getLevelAvgTimeJson(Result result) {
        List<ResultMetric> metrics = resultMetricRepository.findByResult(result);
        Map<String, Double> levelAvgTime = new HashMap<>();

        for (ResultMetric metric : metrics) {
            if (metric.getLevel() != null) {
                levelAvgTime.put(
                        String.valueOf(metric.getLevel().getLevelNo()),
                        metric.getQuestionSolvingTime().doubleValue()
                );
            }
        }

        return toJson(levelAvgTime);
    }

    /**
     * Map → JSON 변환
     */
    private String toJson(Map<String, Double> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
