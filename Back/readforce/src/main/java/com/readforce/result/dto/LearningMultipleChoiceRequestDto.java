package com.readforce.result.dto;

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
public class LearningMultipleChoiceRequestDto {
	
	@NotNull(message = MessageCode.SELECTED_INDEX_NOT_NULL)
	private Integer selectedIndex;
	
	private Long questionSlovingTime;
	
	private Long questionNo;
	
	private Boolean isFavorit;

}
