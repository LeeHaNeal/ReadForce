package com.readforce.member.dto;

import com.readforce.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor

public class MemberKeyInformationDto {

	private final String email;
	
	private final String password;
	
	private final Role role;
	
}
