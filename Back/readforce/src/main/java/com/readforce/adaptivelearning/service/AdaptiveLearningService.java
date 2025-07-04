package com.readforce.adaptivelearning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readforce.adaptivelearning.dto.LearningRecommendationDto;
import com.readforce.adaptivelearning.entity.Question;
import com.readforce.adaptivelearning.entity.Result;
import com.readforce.adaptivelearning.repository.LearningRepository;
import com.readforce.adaptivelearning.repository.QuestionRepository;
import com.readforce.adaptivelearning.repository.ResultRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdaptiveLearningService {

    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final LearningRepository learningRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public LearningRecommendationDto recommend(Long memberNo) {
        Result result = resultRepository.findByMemberNo(memberNo).orElseThrow();
        Set<Long> solved = learningRepository.findSolvedQuestionNosByMemberNo(memberNo);

        List<String> weakCategories = findWeakEntries(result.getCategoryAccuracy(), 0.65);
        List<String> weakTypes = findWeakEntries(result.getTypeAccuracy(), 0.55);
        List<Integer> targetLevels = determineRecommendedLevels(result.getLevelAccuracy(), result.getLevelAvgTime());

        for (String category : weakCategories) {
            for (String type : weakTypes) {
                for (int level : targetLevels) {
                    List<Question> candidates = questionRepository.findByCategoryAndTypeAndLevel(category, type, level);
                    Optional<Question> next = candidates.stream()
                            .filter(q -> !solved.contains(q.getQuestionNo()))
                            .findFirst();
                    if (next.isPresent()) {
                        return LearningRecommendationDto.builder()
                                .category(category)
                                .type(type)
                                .level(level)
                                .question(next.get())
                                .build();
                    }
                }
            }
        }

        // fallback: 아무것도 추천할 수 없을 경우 기본 추천 제공
        return LearningRecommendationDto.builder()
                .category("기본")
                .type("기본")
                .level(1)
                .question(null)
                .build();
    }

    private List<String> findWeakEntries(String json, double threshold) {
        Map<String, Double> map = toMap(json);
        return map.entrySet().stream()
                .filter(e -> e.getValue() <= threshold)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Integer> determineRecommendedLevels(String accuracyJson, String timeJson) {
        Map<String, Double> acc = toMap(accuracyJson);
        Map<String, Double> time = toMap(timeJson);

        List<Integer> levels = acc.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < levels.size() - 1; i++) {
            int a = levels.get(i);
            int b = levels.get(i + 1);
            double accA = acc.getOrDefault(String.valueOf(a), 0.0);
            double accB = acc.getOrDefault(String.valueOf(b), 1.0);

            if (accA >= 0.8 && accB <= 0.5) {
                double timeA = time.getOrDefault(String.valueOf(a), 999.0);
                double avgTime = time.values().stream().mapToDouble(Double::doubleValue).average().orElse(999);
                return Collections.singletonList(timeA <= avgTime ? b : a);
            }
        }

        return List.of(2); // 기본 권장 레벨
    }

    private Map<String, Double> toMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }
}
