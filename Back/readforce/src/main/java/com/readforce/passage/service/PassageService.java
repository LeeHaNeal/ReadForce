package com.readforce.passage.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.repository.PassageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
	
	private final PassageRepository passageRepository;
	
	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategory(String orderBy, String language, String classification, String category) {

		Sort sort = Sort.by(Sort.Direction.fromString(orderBy), "createdAt");
		
		List<PassageResponseDto> passageList = passageRepository.findByLanguage_LanguageAndClassification_ClassificationAndCategory_Category(language, classification, category, sort);
		
		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList;
		
	}

	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategoryAndType(String orderBy, String language, String classification, String category, String type) {

		Sort sort = Sort.by(Sort.Direction.fromString(orderBy), "createdAt");
		
		List<PassageResponseDto> passageList = passageRepository.findByLanguage_LanguageAndClassification_ClassificationAndCategory_CategoryAndType_type(language, classification, category, type, sort);

		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList;
		
	}

	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategoryAndTypeAndLevel(String orderBy, String language, String classification, String category, String type, Integer level) {
		
		Sort sort = Sort.by(Sort.Direction.fromString(orderBy), "createdAt");
		
		List<PassageResponseDto> passageList = passageRepository.findByLanguage_LanguageAndClassification_ClassificationAndCategory_CategoryAndType_typeAndLevel_level(language, classification, category, type, level, sort);

		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList;
		
	}

	@Transactional(readOnly = true)
	public Passage getTestPassage(String language, String category, Integer level) {
		
		long count = passageRepository.countByLanguage_LanguageAndCategory_CategoryAndLevel_Level(language, category, level);
		
		int randomIndex = (int)(Math.random() * count);
		
		Page<Passage> passagePage = passageRepository.findAll(PageRequest.of(randomIndex, 1));
		
		if(passagePage.hasContent()) {
			
			return passagePage.getContent().get(0);
			
		} else {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
	
	}

	public List<Long> getPassageNoListByLanguageAndClassificationAndCategoryAndTypeAndLevel(
			String language,
			String classification, 
			String category, 
			String type, 
			Integer level
	) {

		return passageRepository.findPassageNoListByLanguageAndClassificationAndCategoryAndTypeAndLevel(
				language, classification, category, type, level
		);
		
	}

	@Transactional(readOnly = true)
	public Passage getPassageByPassageNo(Long passageNo) {

		return passageRepository.findById(passageNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND));

	}

	@Transactional
	public void savePassage(
			String title, 
			String content, 
			String author, 
			LocalDate publicationDate,
			Category categoryEntity, 
			Level level, 
			Language language, 
			Classification classificationEntity
	) {

		Passage passage = Passage.builder()
				.title(title)
				.content(content)
				.author(author)
				.publicationDate(publicationDate)
				.category(categoryEntity)
				.level(level)
				.language(language)
				.classification(classificationEntity)
				.build();
		
		passageRepository.save(passage);
		
	}

}
