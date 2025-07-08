package com.readforce.passage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.OrderByEnum;
import com.readforce.common.enums.TypeEnum;
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
	public List<PassageResponseDto> getPassageListByLanguageAndCategory(OrderByEnum orderBy, LanguageEnum language, ClassificationEnum classification, CategoryEnum category) {

		Sort sort = Sort.by(Sort.Direction.fromString(orderBy.name()), "createdAt");
		
		List<Passage> passageList = passageRepository.findByLanguageAndCategoryAndCategory(language, classification, category, sort);
		
		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList.stream()
				.map(PassageResponseDto::new)
				.collect(Collectors.toList());				
		
	}

	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategoryAndType(OrderByEnum orderBy, LanguageEnum language, ClassificationEnum classification, CategoryEnum category, TypeEnum type) {

		Sort sort = Sort.by(Sort.Direction.fromString(orderBy.name()), "createdAt");
		
		List<Passage> passageList = passageRepository.findByLanguageAndCategoryAndCategoryAndType(language, classification, category, type, sort);

		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList.stream()
				.map(PassageResponseDto::new)
				.collect(Collectors.toList());
		
	}

	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategoryAndTypeAndLevel(OrderByEnum orderBy, LanguageEnum language, ClassificationEnum classification, CategoryEnum category, TypeEnum type, Integer level) {
		
		Sort sort = Sort.by(Sort.Direction.fromString(orderBy.name()), "createdAt");
		
		List<Passage> passageList = passageRepository.findByLanguageAndCategoryAndCategoryAndTypeAndLevel(language, classification, category, type, level, sort);

		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList.stream()
				.map(PassageResponseDto::new)
				.collect(Collectors.toList());
		
	}

	@Transactional(readOnly = true)
	public Passage getTestPassage(LanguageEnum language, CategoryEnum category, Integer level) {
		
		long count = passageRepository.countByLanguage_LanguageNameAndCategory_CategoryNameAndLevel_LevelNumber(language, category, level);
		
		int randomIndex = (int)(Math.random() * count);
		
		Page<Passage> passagePage = passageRepository.findAll(PageRequest.of(randomIndex, 1));
		
		if(passagePage.hasContent()) {
			
			return passagePage.getContent().get(0);
			
		} else {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
	
	}

	public List<Long> getPassageNoListByLanguageAndClassificationAndCategoryAndTypeAndLevel(
			LanguageEnum language,
			ClassificationEnum classification, 
			CategoryEnum category, 
			TypeEnum type, 
			Integer level
	) {

		return passageRepository.findPassageNoByLanguageAndClassificationAndCategoryAndTypeAndLevel(
				language, classification, category, type, level
		);
		
	}

	@Transactional(readOnly = true)
	public Passage getPassageByPassageNo(Long passageNo) {

		return passageRepository.findById(passageNo)
				.orElseThrow(() -> new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND));

	}
	@Transactional
	public Passage savePassage(Passage passage) {
	    return passageRepository.save(passage);
	}

	@Transactional
	public Passage savePassage(
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
		
		return passageRepository.save(passage);
		
	}

	@Transactional(readOnly = true)
	public List<Passage> getNoQuestionPassage() {

		List<Passage> noQuestionPassageList = passageRepository.findNoQuestionPassageList();
		
		if(noQuestionPassageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.NO_QUESTION_PASSAGE_NOT_FOUND);
			
		}
		
		return noQuestionPassageList;
		
	}

}
