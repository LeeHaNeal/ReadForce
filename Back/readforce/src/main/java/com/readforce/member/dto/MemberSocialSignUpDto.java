package com.readforce.member.dto;

import java.time.LocalDate;

import com.readforce.common.MessageCode;
import com.readforce.common.validation.ValidBirthday;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor

public class MemberSocialSignUpDto {
	
	@NotBlank(message = MessageCode.TEMPORAL_TOKEN_NOT_BLANK)
	private String temporalToken;
	
	@NotBlank(message = MessageCode.NICKNAME_NOT_BLANK)
	@Size(min = 2, max = 20, message = MessageCode.NICKNAME_SIZE_INVALID)
	@Pattern(regexp = "^[a-zA-Z가-힣\\d]{2,20}$", message = MessageCode.NICKNAME_PATTERN_INVALID)
	private String nickname;
	
	@NotNull(message = MessageCode.BIRTHDAY_NOT_NULL)
	@ValidBirthday
	private LocalDate birthday;
	
}
