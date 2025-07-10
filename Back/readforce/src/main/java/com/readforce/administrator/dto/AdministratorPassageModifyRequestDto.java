package com.readforce.administrator.dto;

import java.time.LocalDate;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.TypeEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorPassageModifyRequestDto {

	@NotNull(message = MessageCode.PASSAGE_NO_NOT_NULL)
	private Long passageNo;
	
	private String title;
	
	private String content;
	
	private String author;
	
	private LocalDate publicationDate;
	
	private CategoryEnum category;
	
	private TypeEnum type;
	
	private Integer level;
	
	private LanguageEnum language;
	
	private ClassificationEnum classification;
	
}
