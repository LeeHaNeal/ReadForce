package com.readforce.adaptivelearning.service;

import com.readforce.adaptivelearning.dto.LearningRecommendationDto;
import com.readforce.adaptivelearning.util.JsonUtil;
import com.readforce.question.entity.Question;
import com.readforce.question.repository.QuestionRepository;
import com.readforce.result.entity.Result;
import com.readforce.result.repository.LearningRepository;
import com.readforce.result.repository.ResultRepository;
import com.readforce.result.service.ResultMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdaptiveLearningService {

    private final ResultRepository resultRepository;
    private final LearningRepository learningRepository;
    private final QuestionRepository questionRepository;
    private final ResultMetricService resultMetricService;

    /**
     * 추천 문제 반환
     */
    public LearningRecommendationDto recommend(Long memberNo) {
        // memberNo 로 result 조회
        Result result = resultRepository.findByMember_MemberNo(memberNo).orElseThrow();

        // 정답률/시간 데이터 가져오기
        String categoryAccuracyJson = resultMetricService.getCategoryAccuracyJson(result);
        String typeAccuracyJson = resultMetricService.getTypeAccuracyJson(result);
        String levelAccuracyJson = resultMetricService.getLevelAccuracyJson(result);
        String levelAvgTimeJson = resultMetricService.getLevelAvgTimeJson(result);

        // 사용자가 이미 푼 문제 번호 조회
        Set<Long> solved = learningRepository.findSolvedQuestionNosByMemberNo(memberNo);

        // 취약 카테고리/타입/레벨 분석
        List<String> weakCategories = findWeakEntries(categoryAccuracyJson, 0.65);
        List<String> weakTypes = findWeakEntries(typeAccuracyJson, 0.55);
        List<Integer> targetLevels = determineRecommendedLevels(levelAccuracyJson, levelAvgTimeJson);

        // 아직 안 푼 문제 중 추천
        for (String category : weakCategories) {
            for (String type : weakTypes) {
                for (int level : targetLevels) {
                    List<Question> candidates = questionRepository.findByPassage_Category_NameAndPassage_Type_NameAndPassage_Level_LevelNo(category, type, level);
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


        // 추천할 문제가 없으면 기본값 반환
        return LearningRecommendationDto.builder()
                .category("기본")
                .type("기본")
                .level(1)
                .question(null)
                .build();
    }

    /**
     * 취약 항목 추출
     */
    private List<String> findWeakEntries(String json, double threshold) {
        Map<String, Double> map = JsonUtil.toMap(json);
        return map.entrySet().stream()
                .filter(e -> e.getValue() <= threshold)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 최적 레벨 추천
     */
    private List<Integer> determineRecommendedLevels(String accuracyJson, String timeJson) {
        Map<String, Double> acc = JsonUtil.toMap(accuracyJson);
        Map<String, Double> time = JsonUtil.toMap(timeJson);

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
}
