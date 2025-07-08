package com.readforce.administrator.dto;

import org.springframework.web.multipart.MultipartFile;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.ClassificationEnum;
import com.readforce.common.enums.LanguageEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorUploadNovelRequestDto {

	@NotNull(message = MessageCode.FILE_NOT_NULL)
	private MultipartFile file;
	
	@NotNull(message = MessageCode.TITLE_NOT_NULL)
	private String title;
	
	@NotNull(message = MessageCode.AUTHOR_NOT_NULL)
	private String author;
	
	@NotNull(message = MessageCode.LANGUAGE_NOT_NULL)
	private LanguageEnum language;
	
	@NotNull(message = MessageCode.LEVEL_NOT_NULL)
	private Integer level;
	
	@NotNull(message = MessageCode.CLASSIFICATION_NOT_NULL)
	private ClassificationEnum classification;
	
}
