package com.readforce.passage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.OrderByEnum;
import com.readforce.common.enums.TypeEnum;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.service.PassageService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/passage")
@RequiredArgsConstructor
public class PassageController {

	private final PassageService passageService;
	
	@GetMapping("/get-passage-list-by-language-and-category")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategory(
			@RequestParam("orderBy")
			@NotNull(message = MessageCode.ORDER_BY_NOT_NULL)
			OrderByEnum orderBy,
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language,
			@RequestParam("classification")
			@NotNull(message = MessageCode.CLASSIFICATION_NOT_NULL)
			ClassificationEnum classification,
			@RequestParam("category")
			@NotNull(message = MessageCode.CATEGORY_NOT_NULL)
			CategoryEnum category
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategory(orderBy, language, classification, category);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);		
		
	}
	
	@GetMapping("/get-passage-list-by-language-and-category-and-type")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategoryAndType(
			@RequestParam("orderBy")
			@NotNull(message = MessageCode.ORDER_BY_NOT_NULL)
			OrderByEnum orderBy,
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language,
			@RequestParam("classification")
			@NotNull(message = MessageCode.CLASSIFICATION_NOT_NULL)
			ClassificationEnum classification,
			@RequestParam("category")
			@NotNull(message = MessageCode.CATEGORY_NOT_NULL)
			CategoryEnum category,
			@RequestParam("type")
			@NotNull(message = MessageCode.TYPE_NOT_NULL)
			TypeEnum type
			
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategoryAndType(orderBy, language, classification, category, type);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);		
		
	}
	
	@GetMapping("/get-passage-list-by-language-and-category-and-type-and-level")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategoryAndTypeAndLevel(
			@RequestParam("orderBy")
			@NotNull(message = MessageCode.ORDER_BY_NOT_NULL)
			OrderByEnum orderBy,
			@RequestParam("language")
			@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
			LanguageEnum language,
			@RequestParam("classification")
			@NotNull(message = MessageCode.CLASSIFICATION_NOT_NULL)
			ClassificationEnum classification,
			@RequestParam("category")
			@NotNull(message = MessageCode.CATEGORY_NOT_NULL)
			CategoryEnum category,
			@RequestParam("type")
			@NotNull(message = MessageCode.TYPE_NOT_NULL)
			TypeEnum type,
			@RequestParam("level")
			@NotNull(message = MessageCode.LEVEL_NOT_NULL)
			@Min(value = 1, message = MessageCode.LEVEL_INVALID)
			@Max(value = 10, message = MessageCode.LEVEL_INVALID)
			Integer level
			
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategoryAndTypeAndLevel(orderBy, language, classification, category, type, level);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);	
		
	}
	
}
