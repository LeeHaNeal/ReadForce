package com.readforce.administrator.dto;

import com.readforce.common.MessageCode;
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
public class AdministratorTypeRequestDto {

	@NotNull(message = MessageCode.TYPE_NOT_NULL)
	private TypeEnum type;
	
}
