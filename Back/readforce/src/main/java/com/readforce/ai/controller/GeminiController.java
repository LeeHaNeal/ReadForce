package com.readforce.ai.controller;

import com.readforce.ai.dto.GeminiRequestDto;
import com.readforce.ai.service.GeminiService;
import com.readforce.common.enums.Category;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.service.PassageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;
    private final PassageService passageService;

    /**
     * 지문 생성 (뉴스 등)
     * - 소설/동화면 null 반환
     */
    @PostMapping("/generate-passage")
    public ResponseEntity<Long> generateAndSavePassage(@RequestBody GeminiRequestDto requestDto) {
        Passage passage = geminiService.generateAndSavePassageIfNeeded(requestDto);
        return ResponseEntity.ok(passage != null ? passage.getPassageNo() : null);
    }

    /**
     * 문제 생성
     * - 소설/동화면 → DB에서 지문 조회 후 문제 생성
     * - 뉴스 등은 → 지문 새로 생성 후 문제 생성
     */
    @PostMapping("/generate-question")
    public ResponseEntity<Long> generateAndSaveQuestion(@RequestBody GeminiRequestDto requestDto) {

        Passage passage;
        if (List.of(Category.NOVEL, Category.FAIRY_TALE).contains(requestDto.getCategory())) {
            // 소설/동화: 기존 지문 사용
            passage = passageService.getPassageByPassageNo(requestDto.getPassageNo());
            requestDto.setPassageText(passage.getContent());
        } else {
            // 뉴스 등: 새 지문 생성
            passage = geminiService.generateAndSavePassageIfNeeded(requestDto);
            requestDto.setPassageText(passage.getContent());
        }

        var question = geminiService.generateAndSaveQuestion(requestDto, passage);
        return ResponseEntity.ok(question.getQuestionNo());
    }
}
