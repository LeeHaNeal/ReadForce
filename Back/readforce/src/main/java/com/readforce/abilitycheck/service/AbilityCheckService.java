package com.readforce.abilitycheck.service;

import com.readforce.abilitycheck.dto.AnswerRequestDto;
import com.readforce.abilitycheck.dto.ComprehensionRequestDto;
import com.readforce.abilitycheck.dto.EvaluationResultDto;
import com.readforce.abilitycheck.util.VocabularyEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AbilityCheckService {

    // 사용자가 푼 문제 캐싱 (DB 저장 안 함)
    private final Map<Long, Set<Long>> solvedQuestionCache = new ConcurrentHashMap<>();

    /**
     * 어휘력 평가
     */
    public String evaluate(AnswerRequestDto req) {
        if (isDuplicate(req.getMemberNo(), req.getQuestionNo())) {
            return "DUPLICATE";
        }
        markAsSolved(req.getMemberNo(), req.getQuestionNo());

        int nextLevel = VocabularyEvaluator.evaluate(req.getCurrentLevel(), req.isCorrect());
        String timeFeedback = getTimeFeedback(req.getSolvingTime());

        return (nextLevel < 0 ? "COMPLETE:" + (-nextLevel) : String.valueOf(nextLevel)) + " | " + timeFeedback;
    }

    /**
     * 사실적 이해력 평가
     */
    public String factualEvaluate(ComprehensionRequestDto req) {
        if (isDuplicate(req.getMemberNo(), req.getQuestionNo())) {
            return "이미 푼 문제입니다.";
        }
        markAsSolved(req.getMemberNo(), req.getQuestionNo());

        return req.isCorrect() ? "다음 단계: 추론적 이해력 확인" : "사실적 이해력 부족 - 종료";
    }

    /**
     * 추론적 이해력 평가 및 최종 결과 반환
     */
    public EvaluationResultDto inferentialEvaluate(ComprehensionRequestDto req) {
        if (isDuplicate(req.getMemberNo(), req.getQuestionNo())) {
            return new EvaluationResultDto(
                req.getVocabularyLevel(),
                req.isFactualCorrect(),
                req.isCorrect(),
                "이미 푼 문제입니다."
            );
        }
        markAsSolved(req.getMemberNo(), req.getQuestionNo());

        int level = req.getVocabularyLevel();
        boolean factual = req.isFactualCorrect();
        boolean inferential = req.isCorrect();

        String summary = generateFinalSummary(level, factual, inferential);
        return new EvaluationResultDto(level, factual, inferential, summary);
    }

    /**
     * 최종 결과 메시지 생성
     */
    private String generateFinalSummary(int level, boolean factual, boolean inferential) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("사용자의 어휘력 레벨은 %d입니다. ", level));

        if (factual && inferential) {
            sb.append("사용자는 단어의 뜻을 잘 알고 있으며, 글에 쓰여 있는 내용을 정확하게 찾아내는 능력이 뛰어나네요. ");
            sb.append("또 글쓴이가 직접 말하지 않은 숨은 의미를 찾아내는 능력도 뛰어나요.");
        } else if (factual && !inferential) {
            sb.append("사용자는 단어의 뜻을 잘 알고 있으며, 글에 쓰여 있는 내용을 정확하게 찾아내는 능력이 뛰어나네요. ");
            sb.append("또 글쓴이가 직접 말하지 않은 숨은 의미를 찾아내는 능력은 부족해요.");
        } else if (!factual && inferential) {
            sb.append("사용자는 글쓴이가 직접 말하지 않은 숨은 의미는 잘 찾아내지만, ");
            sb.append("글에 쓰여 있는 내용을 정확하게 이해하는 데는 어려움이 있어요.");
        } else {
            sb.append("사용자는 단어의 뜻과 글에 쓰여 있는 내용을 정확하게 이해하는 데 모두 어려움이 있어요.");
        }

        return sb.toString();
    }

    /**
     * 풀이 시간에 따른 피드백
     */
    private String getTimeFeedback(long solvingTime) {
        if (solvingTime < 5000) return "⚠️ 너무 빨리 제출했어요. 문제를 충분히 읽었나요?";
        else if (solvingTime < 15000) return "⏱️ 적절한 시간에 풀었어요.";
        else return "⌛ 시간이 오래 걸렸어요. 집중해서 읽어보는 연습이 필요해요.";
    }

    /**
     * 중복 풀이 여부 확인
     */
    private boolean isDuplicate(Long memberNo, Long questionNo) {
        return solvedQuestionCache.getOrDefault(memberNo, Collections.emptySet()).contains(questionNo);
    }

    /**
     * 문제 풀이 완료로 마킹
     */
    private void markAsSolved(Long memberNo, Long questionNo) {
        solvedQuestionCache.computeIfAbsent(memberNo, k -> new HashSet<>()).add(questionNo);
    }
}
