package com.readforce.member.dto;

import java.time.LocalDate;

import com.readforce.common.MessageCode;
import com.readforce.common.validation.ValidBirthday;

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
public class MemberModifyDto {

	@Size(min = 2, max = 12, message = MessageCode.NICKNAME_SIZE_INVALID)
	@Pattern(regexp = "^[a-zA-Z가-힣\\d]{2,20}$", message = MessageCode.NICKNAME_PATTERN_INVALID)
	private String nickname;
	
	@ValidBirthday
	private LocalDate birthday;
	
}
