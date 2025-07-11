package com.readforce.administrator.dto;

import com.readforce.common.MessageCode;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorAgeGroupRequestDto {

	@NotNull(message = MessageCode.AGE_GROUP_NOT_NULL)
	private Integer ageGroup;
	
}
