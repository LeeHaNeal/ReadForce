package com.readforce.abilitycheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.abilitycheck.dto.AnswerRequestDto;
import com.readforce.abilitycheck.dto.ComprehensionRequestDto;
import com.readforce.abilitycheck.dto.EvaluationResultDto;
import com.readforce.abilitycheck.service.AbilityCheckService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/ability")
@RequiredArgsConstructor
public class AbilityCheckController {

    private final AbilityCheckService abilityCheckService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitAnswer(@Valid @RequestBody AnswerRequestDto request) {
        return ResponseEntity.ok(abilityCheckService.evaluate(request));
    }

    @PostMapping("/factual")
    public ResponseEntity<String> factualCheck(@Valid @RequestBody ComprehensionRequestDto req) {
        return ResponseEntity.ok(abilityCheckService.factualEvaluate(req));
    }

    @PostMapping("/inferential")
    public ResponseEntity<EvaluationResultDto> inferentialCheck(@Valid @RequestBody ComprehensionRequestDto req) {
        return ResponseEntity.ok(abilityCheckService.inferentialEvaluate(req));
    }

}
