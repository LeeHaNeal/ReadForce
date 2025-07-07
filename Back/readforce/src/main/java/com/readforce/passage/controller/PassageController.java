package com.readforce.passage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.Category;
import com.readforce.common.enums.Classification;
import com.readforce.common.enums.Language;
import com.readforce.common.enums.OrderBy;
import com.readforce.common.enums.Type;
import com.readforce.passage.dto.PassageResponseDto;
import com.readforce.passage.service.PassageService;
import com.readforce.passage.validation.ValidEnum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/passage")
@RequiredArgsConstructor
public class PassageController {

	private final PassageService passageService;
	
	@GetMapping("/get-passage-list-by-language-and-category")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategory(
			@RequestParam("orderBy")
			@NotBlank(message = MessageCode.ORDER_BY_NOT_BLANK)
			@ValidEnum(enumClass = OrderBy.class, message = MessageCode.ORDER_BY_INVALID)
			String orderBy,
			@RequestParam("language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language,
			@RequestParam("classification")
			@NotBlank(message = MessageCode.CLASSIFICATION_NOT_BLANK)
			@ValidEnum(enumClass = Classification.class, message = MessageCode.CLASSIFICATION_INVALID)
			String classification,
			@RequestParam("category")
			@NotBlank(message = MessageCode.CATEGORY_NOT_BLANK)
			@ValidEnum(enumClass = Category.class, message = MessageCode.CATEGORY_INVALID)
			String category
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategory(orderBy, language, classification, category);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);		
		
	}
	
	@GetMapping("/get-passage-list-by-language-and-category-and-type")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategoryAndType(
			@RequestParam("orderBy")
			@NotBlank(message = MessageCode.ORDER_BY_NOT_BLANK)
			@ValidEnum(enumClass = OrderBy.class, message = MessageCode.ORDER_BY_INVALID)
			String orderBy,
			@RequestParam("language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language,
			@RequestParam("classification")
			@NotBlank(message = MessageCode.CLASSIFICATION_NOT_BLANK)
			@ValidEnum(enumClass = Classification.class, message = MessageCode.CLASSIFICATION_INVALID)
			String classification,
			@RequestParam("category")
			@NotBlank(message = MessageCode.CATEGORY_NOT_BLANK)
			@ValidEnum(enumClass = Category.class, message = MessageCode.CATEGORY_INVALID)
			String category,
			@RequestParam("type")
			@NotBlank(message = MessageCode.TYPE_NOT_BLANK)
			@ValidEnum(enumClass = Type.class, message = MessageCode.TYPE_INVALID)
			String type
			
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategoryAndType(orderBy, language, classification, category, type);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);		
		
	}
	
	@GetMapping("/get-passage-list-by-language-and-category-and-type-and-level")
	public ResponseEntity<List<PassageResponseDto>> getPassageListByLanguageAndCategoryAndTypeAndLevel(
			@RequestParam("orderBy")
			@NotBlank(message = MessageCode.ORDER_BY_NOT_BLANK)
			@ValidEnum(enumClass = OrderBy.class, message = MessageCode.ORDER_BY_INVALID)
			String orderBy,
			@RequestParam("language")
			@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
			@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
			String language,
			@RequestParam("classification")
			@NotBlank(message = MessageCode.CLASSIFICATION_NOT_BLANK)
			@ValidEnum(enumClass = Classification.class, message = MessageCode.CLASSIFICATION_INVALID)
			String classification,
			@RequestParam("category")
			@NotBlank(message = MessageCode.CATEGORY_NOT_BLANK)
			@ValidEnum(enumClass = Category.class, message = MessageCode.CATEGORY_INVALID)
			String category,
			@RequestParam("type")
			@NotBlank(message = MessageCode.TYPE_NOT_BLANK)
			@ValidEnum(enumClass = Type.class, message = MessageCode.TYPE_INVALID)
			String type,
			@RequestParam("level")
			@NotBlank(message = MessageCode.LEVEL_NOT_BLANK)
			@Min(value = 1, message = MessageCode.LEVEL_INVALID)
			@Max(value = 10, message = MessageCode.LEVEL_INVALID)
			Integer level
			
	){
		
		List<PassageResponseDto> passageList = 
				passageService.getPassageListByLanguageAndCategoryAndTypeAndLevel(orderBy, language, classification, category, type, level);
		
		
		return ResponseEntity.status(HttpStatus.OK).body(passageList);	
		
	}
	
}
