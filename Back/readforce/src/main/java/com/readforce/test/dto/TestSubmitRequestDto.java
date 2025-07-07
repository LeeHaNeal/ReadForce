package com.readforce.test.dto;

import com.readforce.common.MessageCode;
import com.readforce.common.enums.Language;
import com.readforce.passage.validation.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestSubmitRequestDto {
	
	@NotBlank(message = MessageCode.TESTER_ID_NOT_BLANK)
	private String testerId;

	@NotNull(message = MessageCode.QUESTION_NO_NOT_NULL)
	private Long questionNo;
	
	@NotNull(message = MessageCode.SELECTED_INDEX_NOT_NULL)
	private Integer selectedIndex;
	
	@NotNull(message = MessageCode.QUESTION_SOLVING_TIME_NOT_NULL)
	private Long questionSolvingTime;
	
	@NotBlank(message = MessageCode.LANGUAGE_NOT_BLANK)
	@ValidEnum(enumClass = Language.class, message = MessageCode.LANGUAGE_INVALID)
	private String language;
}
