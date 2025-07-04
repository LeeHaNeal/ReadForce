package com.readforce.abilitycheck.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AnswerRequestDto {

    @NotNull
    private Long memberNo;

    @NotNull
    private Long questionNo;

    private boolean isCorrect;

    @PositiveOrZero
    private long solvingTime;

    private int currentLevel;
}
