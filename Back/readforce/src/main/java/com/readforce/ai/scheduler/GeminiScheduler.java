package com.readforce.ai.scheduler;

import com.readforce.ai.dto.GeminiRequestDto;
import com.readforce.ai.service.GeminiService;
import com.readforce.common.enums.Category;
import com.readforce.common.enums.Classification;
import com.readforce.common.enums.Language;
import com.readforce.common.enums.Type;
import com.readforce.passage.entity.Passage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiScheduler {

    private final GeminiService geminiService;

    /**
     * 매일 오전 3시에 모든 언어 및 레벨에 대해 뉴스 지문 및 문제 생성
     */
    @Scheduled(cron = "0 54 13 * * *")  // 매일 오전 3시
    @Transactional
    public void generateAllPassagesAndQuestions() {
        for (Language language : Language.values()) {
            for (int level = 1; level <= 10; level++) {
                try {
                    GeminiRequestDto req = GeminiRequestDto.builder()
                            .category(Category.NEWS)
                            .type(Type.POLITICS)  // 필요에 따라 타입 변경 가능
                            .language(language)
                            .classification(Classification.NORMAL)
                            .level(level)
                            .build();

                    Passage passage = geminiService.generateAndSavePassage(req);
                    geminiService.generateAndSaveQuestion(req, passage);

                    log.info("✅ [Scheduler] 생성 완료 - 언어: {}, 레벨: {}", language, level);
                } catch (Exception e) {
                    log.error("❌ [Scheduler] 생성 실패 - 언어: {}, 레벨: {}", language, level, e);
                }
            }
        }
    }

    /**
     * (옵션) 소설 문제 생성 예제
     */
    // @Scheduled(cron = "0 0 4 * * *")  // 필요시 주석 해제 후 활성화
    @Transactional
    public void generateFictionQuestion() {
        try {
            Long passageNo = 1L;
            Passage passage = geminiService.getPassageById(passageNo);

            GeminiRequestDto req = GeminiRequestDto.builder()
                    .category(Category.NOVEL)
                    .language(Language.KOREAN)
                    .classification(Classification.NORMAL)
                    .level(3)
                    .passageText(passage.getContent())
                    .build();

            geminiService.generateAndSaveQuestion(req, passage);

            log.info("✅ [Scheduler] 소설 문제 생성 완료 (passageNo={})", passageNo);
        } catch (Exception e) {
            log.error("❌ [Scheduler] 소설 문제 생성 실패", e);
        }
    }
}
