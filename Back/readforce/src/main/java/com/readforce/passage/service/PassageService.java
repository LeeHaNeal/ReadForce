package com.readforce.passage.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.administrator.dto.AdministratorUploadPassageRequestDto;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.OrderByEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.file.service.FileService;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.entity.Category;
import com.readforce.passage.entity.Classification;
import com.readforce.passage.entity.Language;
import com.readforce.passage.entity.Level;
import com.readforce.passage.entity.Passage;
import com.readforce.passage.entity.Type;
import com.readforce.passage.repository.PassageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassageService {
	
	private final PassageRepository passageRepository;
	private final LanguageService languageService;
	private final CategoryService categoryService;
	private final LevelService levelService;
	private final ClassificationService classificationService;
	private final TypeService typeService;
	private final FileService fileService;
	
	@Transactional(readOnly = true)
	public List<PassageResponseDto> getPassageListByLanguageAndCategory(OrderByEnum orderBy, LanguageEnum language, ClassificationEnum classification, CategoryEnum category) {

		Sort sort = Sort.by(Sort.Direction.fromString(orderBy.name()), "createdAt");
		
		List<Passage> passageList = passageRepository.findByLanguageAndCategory(language, classification, category, sort);
		
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
		
		long count = passageRepository.countByLanguage_LanguageNameAndCategory_CategoryNameAndLevel_LevelNumberAndClassification_ClassificationName(language, category, level, ClassificationEnum.TEST);
		
		if(count == 0) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		int randomIndex = (int)(Math.random() * count);
		
		Page<Passage> passagePage = passageRepository.findByLanguageAndCategoryAndLevelAndClassification(language, category, level, ClassificationEnum.TEST, PageRequest.of(randomIndex, 1));
		
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
	public Passage savePassage(
			String title, 
			String content, 
			String author, 
			LocalDate publicationDate,
			Category categoryEntity, 
			Level level, 
			Language language, 
			Classification classificationEntity,
			Type type
	) {

		if(type != null) {
			
			Passage passage = Passage.builder()
					.title(title)
					.content(content)
					.author(author)
					.publicationDate(publicationDate)
					.category(categoryEntity)
					.level(level)
					.language(language)
					.type(type)
					.classification(classificationEntity)
					.build();
			
			return passageRepository.save(passage);
			
		} else {
			
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

	}

	@Transactional(readOnly = true)
	public List<Passage> getNoQuestionPassage() {

		List<Passage> noQuestionPassageList = passageRepository.findNoQuestionPassageList();
		
		if(noQuestionPassageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.NO_QUESTION_PASSAGE_NOT_FOUND);
			
		}
		
		return noQuestionPassageList;
		
	}

	@Transactional
	public void uploadPassage(AdministratorUploadPassageRequestDto requestDto) {
		
		Language language = languageService.getLangeageByLanguage(requestDto.getLanguage());
		Category category = categoryService.getCategoryByCategory(requestDto.getCategory());
		Level level = levelService.getLevelByLevel(requestDto.getLevel());
		Classification classification = classificationService.getClassificationByClassfication(requestDto.getClassification());
		Type type = typeService.getTypeByType(requestDto.getType());
		
		savePassage(
				requestDto.getTitle(),
				requestDto.getContent(),
				requestDto.getAuthor(),
				LocalDate.now(),
				category,
				level,
				language,
				classification,
				type
		);
		
	}

	@Transactional(readOnly = true)
	public List<Passage> getChallengePassageList(LanguageEnum language, CategoryEnum category, Integer level) {

		List<Long> passageNoList = passageRepository.findPassageNoByLanguageAndCategoryAndLevelAndClassification(
				language,
				category,
				level,
				ClassificationEnum.CHALLENGE		
		);
		
		if(passageNoList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		Collections.shuffle(passageNoList);
		
		List<Long> randomPassageNoList = passageNoList.stream().limit(2).collect(Collectors.toList());
		
		return passageRepository.findAllById(randomPassageNoList);

	}

	public List<PassageResponseDto> getPassageListByLanguageAndCategoryAndLevel(
			OrderByEnum orderBy, 
			LanguageEnum language,
			ClassificationEnum classification,
			CategoryEnum category,
			Integer level
	) {
		
		Sort sort = Sort.by(Sort.Direction.fromString(orderBy.name()), "createdAt");
		
		List<Passage> passageList = passageRepository.findByLanguageAndCategoryAndLevel(language, classification, category, level, sort);
		
		if(passageList.isEmpty()) {
			
			throw new ResourceNotFoundException(MessageCode.PASSAGE_NOT_FOUND);
			
		}
		
		return passageList.stream()
				.map(PassageResponseDto::new)
				.collect(Collectors.toList());	

	}

	@Transactional(readOnly = true)
	public List<Passage> getNormalPassages(
			LanguageEnum languageName,
			CategoryEnum categoryName, 
			Integer levelNumber
	) {
		
		return passageRepository.findByLanguageAndCategoryAndLevelAndClassification(
				languageName, 
				categoryName, 
				levelNumber, 
				ClassificationEnum.NORMAL, 
				PageRequest.of(0, Integer.MAX_VALUE)).getContent();

	}
	
	@Transactional(readOnly = true)
	public List<Passage> getAllPassagesByClassification(ClassificationEnum classificationName){
		
		return passageRepository.findAllByClassification_ClassificationName(classificationName, Pageable.unpaged()).getContent();
		
	}

}