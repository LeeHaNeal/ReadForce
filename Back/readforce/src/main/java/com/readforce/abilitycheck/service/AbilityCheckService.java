package com.readforce.abilitycheck.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.readforce.abilitycheck.dto.AnswerRequestDto;
import com.readforce.abilitycheck.dto.ComprehensionRequestDto;
import com.readforce.abilitycheck.dto.EvaluationResultDto;
import com.readforce.abilitycheck.util.VocabularyEvaluator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AbilityCheckService {

    private final Map<Long, Set<Long>> solvedQuestionCache = new ConcurrentHashMap<>();

    // 어휘력 평가 (풀이 시간 반영)
    public String evaluate(AnswerRequestDto req) {
        if (isDuplicate(req.getMemberNo(), req.getQuestionNo())) {
            return "DUPLICATE";
        }
        markAsSolved(req.getMemberNo(), req.getQuestionNo());

        int next = VocabularyEvaluator.evaluate(req.getCurrentLevel(), req.isCorrect());

        String timeFeedback = getTimeFeedback(req.getSolvingTime());
        return (next < 0 ? "COMPLETE:" + (-next) : String.valueOf(next)) + " | " + timeFeedback;
    }

    public String factualEvaluate(ComprehensionRequestDto req) {
        if (isDuplicate(req.getMemberNo(), req.getQuestionNo())) {
            return "이미 푼 문제입니다.";
        }
        markAsSolved(req.getMemberNo(), req.getQuestionNo());

        return req.isCorrect() ? "다음 단계: 추론적 이해력 확인" : "사실적 이해력 부족 - 종료";
    }

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

        StringBuilder summary = new StringBuilder();

        if (factual && inferential) {
            summary.append("🟢 사용자는 단어의 의미를 잘 파악하고, 글에서 중요한 정보를 정확히 찾을 수 있으며, ");
            summary.append("글쓴이의 숨은 의도나 맥락도 능숙하게 이해합니다.");
        } else if (factual && !inferential) {
            summary.append("🟡 사용자는 단어의 의미와 글의 사실적인 내용을 잘 이해하지만, ");
            summary.append("글쓴이의 숨은 의도나 맥락을 파악하는 데는 다소 어려움이 있습니다.");
        } else if (!factual && inferential) {
            summary.append("🟡 사용자는 글쓴이의 숨은 의도는 잘 파악하지만, 글의 사실적인 내용을 정확히 이해하는 데는 어려움이 있습니다.");
        } else {
            summary.append("🔴 사용자는 어휘, 사실, 맥락 등 글 전반에 대한 이해가 부족한 편입니다. ");
            summary.append("지속적인 독서 훈련을 통해 기본 독해력을 키워보세요.");
        }

        return new EvaluationResultDto(level, factual, inferential, summary.toString());
    }

    private boolean isDuplicate(Long memberNo, Long questionNo) {
        return solvedQuestionCache.getOrDefault(memberNo, Collections.emptySet()).contains(questionNo);
    }

    private void markAsSolved(Long memberNo, Long questionNo) {
        solvedQuestionCache.computeIfAbsent(memberNo, k -> new HashSet<>()).add(questionNo);
    }

    // 풀이 시간에 따른 피드백 메시지 생성
    private String getTimeFeedback(long solvingTime) {
        if (solvingTime < 5000) return "⚠️ 너무 빨리 제출했어요. 문제를 충분히 읽었나요?";
        else if (solvingTime < 15000) return "⏱️ 적절한 시간에 풀었어요.";
        else return "⌛ 시간이 오래 걸렸어요. 집중해서 읽어보는 연습이 필요해요.";
    }
}
