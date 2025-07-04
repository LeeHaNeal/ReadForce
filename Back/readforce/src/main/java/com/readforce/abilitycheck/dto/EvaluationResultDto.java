package com.readforce.abilitycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResultDto {
    private int vocabularyLevel;
    private boolean factual;
    private boolean inferential;
    private String summary;
}
