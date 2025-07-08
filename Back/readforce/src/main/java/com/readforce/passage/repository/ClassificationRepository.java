package com.readforce.passage.repository;

import com.readforce.passage.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationRepository extends JpaRepository<Classification, Long> {

    // Classification 엔티티의 classification 필드로 조회
    Classification findByClassification(com.readforce.common.enums.Classification classification);
}
