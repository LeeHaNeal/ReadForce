package com.readforce.adaptivelearning.controller;

import com.readforce.adaptivelearning.dto.LearningRecommendationDto;
import com.readforce.adaptivelearning.service.AdaptiveLearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adaptive-learning")
public class AdaptiveLearningController {

    private final AdaptiveLearningService adaptiveLearningService;

    @GetMapping("/recommend/{memberNo}")
    public LearningRecommendationDto recommend(@PathVariable Long memberNo) {
        return adaptiveLearningService.recommend(memberNo);
    }
}
