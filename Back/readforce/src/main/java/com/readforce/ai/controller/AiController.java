package com.readforce.ai.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.ai.dto.AiGeneratePassageRequestDto;
import com.readforce.ai.service.AiService;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.LanguageEnum;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Validated
public class AiController {

    private final AiService aiService;

    /**
     * 테스트용 어휘 및 문제 생성
     */
    @PostMapping("/generate-test")
    public ResponseEntity<Map<String, String>> generateTest(
            @RequestParam("language")
            @NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
            LanguageEnum language
    ) {
        aiService.generateTestVocabulary(language);
        aiService.generateTestQuestion(language);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.GENERATE_TEST_SUCCESS
        ));
    }

    /**
     * 일반 지문 생성
     */
    @PostMapping("/generate-passage")
    public ResponseEntity<Map<String, String>> generatePassage(
            @RequestBody AiGeneratePassageRequestDto aiGeneratePassageRequestDto
    ) {
        aiService.generatePassage(aiGeneratePassageRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.GENERATE_PASSAGE_SUCCESS
        ));
    }

    /**
     * 미사용 지문으로 문제 생성
     */
    @PostMapping("/generate-question")
    public ResponseEntity<Map<String, String>> generateQuestion() {
        aiService.generateQuestion();

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.GENERATE_QUESTION_SUCCESS
        ));
    }

    /**
     * 관리자용: 창의 뉴스 퀴즈 생성
     */
    @PostMapping("/generate-creative-news-quiz")
    public ResponseEntity<Map<String, String>> generateCreativeNewsQuiz() {
        aiService.generateTestVocabulary(LanguageEnum.KOREAN);
        aiService.generateTestQuestion(LanguageEnum.KOREAN);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.GENERATE_TEST_SUCCESS
        ));
    }
}
