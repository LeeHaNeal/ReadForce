package com.readforce.adaptivelearning.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Learning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long learningNo;

    private Boolean isCorrect;

    private Long questionSolvingTime;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastModifiedAt = LocalDateTime.now();

    private Long questionNo;

    private Long memberNo;

    private Boolean isTest = true;  // 능력검사는 항상 테스트 지문 기반
}
