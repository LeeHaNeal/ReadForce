package com.readforce.abilitycheck.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComprehensionRequestDto {

    @NotNull
    private Long memberNo;

    @NotNull
    private Long questionNo;

    private boolean isCorrect;

    private int vocabularyLevel;

    private boolean factualCorrect;
}
