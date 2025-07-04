package com.readforce.adaptivelearning.dto;

import com.readforce.adaptivelearning.entity.Question;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningRecommendationDto {
    private String category;
    private String type;
    private int level;
    private Question question;
}
