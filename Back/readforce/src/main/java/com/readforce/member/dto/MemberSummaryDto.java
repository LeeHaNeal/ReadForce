package com.readforce.member.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberSummaryDto {

	private String email;
	
	private String nickname;
	
	private String socialProvider;
	
	private LocalDate birthday;
	
}
