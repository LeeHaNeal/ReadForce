package com.readforce.adaptivelearning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionNo;

    private String category;
    private String type;
    private Integer level;

    @Lob
    private String content;
}
