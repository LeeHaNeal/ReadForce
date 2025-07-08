package com.readforce.passage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.repository.ClassificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassificationService {
	
	private final ClassificationRepository classificationRepository;
	
	@Transactional(readOnly = true)
	public Classification getClassificationByClassfication(ClassificationEnum classification) {
		
		return classificationRepository.findByClassificationName(classification)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.CLASSIFICATION_NOT_FOUND));
				
	}

}