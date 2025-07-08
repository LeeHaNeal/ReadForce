package com.readforce.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiQuestionDto {
    private String questionText;
    private List<String> choices;
    private String correctAnswer;
    private String explanation;
}
