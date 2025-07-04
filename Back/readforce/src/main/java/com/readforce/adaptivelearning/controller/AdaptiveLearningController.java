package com.readforce.adaptivelearning.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.readforce.adaptivelearning.dto.LearningRecommendationDto;
import com.readforce.adaptivelearning.service.AdaptiveLearningService;

@RestController
@RequestMapping("/api/adaptive")
@RequiredArgsConstructor
public class AdaptiveLearningController {

    private final AdaptiveLearningService service;

    @GetMapping("/recommend")
    public ResponseEntity<LearningRecommendationDto> recommend(@RequestParam Long memberNo) {
        return ResponseEntity.ok(service.recommend(memberNo));
    }
}

