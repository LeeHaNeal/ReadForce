package com.readforce.member.dto;

import com.readforce.common.MessageCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberPasswordResetFromLinkDto {

	@NotBlank(message = MessageCode.TEMPORAL_TOKEN_NOT_BLANK)
	private String temporalToken;
	
	@NotBlank(message = MessageCode.PASSWORD_NOT_BLANK)
	@Size(min = 8, max = 20, message = MessageCode.PASSWORD_SIZE_INVALID)
	@Pattern(
	        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_=+]).*$"
	        , message = MessageCode.PASSWORD_PATTERN_INVALID
	)
	private String newPassword;
	
}
