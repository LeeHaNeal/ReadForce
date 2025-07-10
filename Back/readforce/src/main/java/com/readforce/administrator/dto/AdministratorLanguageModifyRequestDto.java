package com.readforce.administrator.dto;

import com.readforce.common.enums.LanguageEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorLanguageModifyRequestDto {

	private Long languageNo;
	
	private LanguageEnum language;
	
}
