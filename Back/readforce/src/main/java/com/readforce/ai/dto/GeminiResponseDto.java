package com.readforce.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {
    // Passage 관련
    private String title;
    private String passageText;

    // Question 관련
    private String questionText;
    private List<String> choices;
    private String correctAnswer;
    private String explanation;

    // 공통 정보 (Passage용)
    private String author;
    private LocalDate publicationDate;
}
