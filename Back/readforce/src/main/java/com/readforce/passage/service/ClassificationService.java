package com.readforce.passage.service;

import com.readforce.passage.entity.Classification;
import com.readforce.passage.repository.ClassificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    /**
     * String 으로 넘어온 classification 값을 Enum 으로 변환 후 조회
     */
    public Classification getClassificationByEnum(String classification) {
        try {
            com.readforce.common.enums.Classification enumValue =
                    com.readforce.common.enums.Classification.valueOf(classification.toUpperCase());

            return classificationRepository.findByClassification(enumValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 Classification 값: " + classification);
        }
    }
}
