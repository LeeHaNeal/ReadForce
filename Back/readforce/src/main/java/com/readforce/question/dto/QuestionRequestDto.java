package com.readforce.question.dto;

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
public class QuestionRequestDto {

	@NotNull(message = MessageCode.PASSAGE_NO_NOT_NULL)
	private Long passageNo;
	
}
