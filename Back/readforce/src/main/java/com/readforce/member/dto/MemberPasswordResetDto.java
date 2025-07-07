package com.readforce.member.dto;

import com.readforce.common.MessageCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordResetDto {

	@NotBlank(message = MessageCode.PASSWORD_NOT_BLANK)
	@Size(min = 8, max = 20, message = MessageCode.PASSWORD_SIZE_INVALID)
	@Pattern(
	        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_=+]).*$"
	        , message = MessageCode.PASSWORD_PATTERN_INVALID
	)
	private String oldPassword;
	
	@NotBlank(message = MessageCode.PASSWORD_NOT_BLANK)
	@Size(min = 8, max = 20, message = MessageCode.PASSWORD_SIZE_INVALID)
	@Pattern(
	        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()-_=+]).*$"
	        , message = MessageCode.PASSWORD_PATTERN_INVALID
	)
	private String newPassword;
	
}
