package com.readforce.adaptivelearning.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultNo;

    private Integer consecutiveLearningDays;
    private Double overallAccuracy;

    @Column(columnDefinition = "jsonb")
    private String categoryAccuracy;

    @Column(columnDefinition = "jsonb")
    private String categoryAvgTime;

    @Column(columnDefinition = "jsonb")
    private String typeAccuracy;

    @Column(columnDefinition = "jsonb")
    private String typeAvgTime;

    @Column(columnDefinition = "jsonb")
    private String levelAccuracy;

    @Column(columnDefinition = "jsonb")
    private String levelAvgTime;

    @Column(columnDefinition = "jsonb")
    private String languageAccuracy;

    @Column(columnDefinition = "jsonb")
    private String languageAvgTime;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastModifiedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Long memberNo; // ✅ 이메일 대신 멤버 번호로 전환
}
