package com.readforce.administrator.dto;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.ClassificationEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorClassificationRequestDto {

	@NotNull(message = MessageCode.CLASSIFICATION_NOT_NULL)
	private ClassificationEnum classification;
	
}
