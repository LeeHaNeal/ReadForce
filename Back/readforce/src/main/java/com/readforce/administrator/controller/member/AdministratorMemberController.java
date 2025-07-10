package com.readforce.administrator.controller.member;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readforce.administrator.dto.AdministratorAddMemberRequestDto;
import com.readforce.administrator.dto.AdministratorMemberResponseDto;
import com.readforce.administrator.dto.AdministratorModifyRequestDto;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.RoleEnum;
import com.readforce.member.entity.Member;
import com.readforce.member.service.MemberService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/administrator/member")
@RequiredArgsConstructor
@Validated
public class AdministratorMemberController {
	
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/get-all-member-list")
	public ResponseEntity<List<AdministratorMemberResponseDto>> getAllMemberList(){
		
		List<AdministratorMemberResponseDto> memberList = memberService.getAllMemberList();
		
		return ResponseEntity.status(HttpStatus.OK).body(memberList);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/add-member")
	public ResponseEntity<Map<String, String>> addMember(
			@Valid @RequestBody AdministratorAddMemberRequestDto requestDto
	){
		
		memberService.saveMember(Member.builder()
				.email(requestDto.getEmail())
				.password(passwordEncoder.encode(requestDto.getPassword()))
				.nickname(requestDto.getNickname())
				.birthday(requestDto.getBirthday())
				.role(requestDto.getRole() != null ? requestDto.getRole() : RoleEnum.USER)
				.build()	
		);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.ADD_MEMBER_SUCCESS
		));
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/modify")
	public ResponseEntity<Map<String, String>> modify(
			@Valid @RequestBody AdministratorModifyRequestDto requestDto			
	){
		
		memberService.modifyByAdmin(requestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.MEMBER_MODIFY_SUCCESS
		));		
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> delete(
			@RequestParam("email")
			@NotBlank(message = MessageCode.EMAIL_NOT_BLANK)
			@Email
			String email
	){
		
		memberService.deleteMemberByAdmin(email);
		
		return ResponseEntity.status(HttpStatus.OK).body(Map.of(
				MessageCode.MESSAGE_CODE, MessageCode.DELETE_MEMBER_SUCCESS
		));
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/get-member")
	public ResponseEntity<AdministratorMemberResponseDto> getMember(
			@RequestParam("email")
			@NotBlank(message = MessageCode.EMAIL_NOT_BLANK)
			@Email
			String email	
	){
		
		Member member = memberService.getActiveMemberByEmail(email);
		
		AdministratorMemberResponseDto memberDto = AdministratorMemberResponseDto.builder()
				.email(member.getEmail())
				.nickname(member.getNickname())
				.birthday(member.getBirthday())
				.profileImagePath(member.getProfileImagePath())
				.status(member.getStatus().name())
				.role(member.getRole().name())
				.socialProvider(member.getSocialProvider())
				.createdAt(member.getCreatedAt())
				.lastModifiedAt(member.getLastModifiedAt())
				.withdrawAt(member.getWithdrawAt())
				.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(memberDto);
		
	}
	
	// 지문 수정/삭제
	
	// 문제 수정/삭제
	
	// 평균 풀이 시간 조회/생성/수정/삭제
	
	// 연령대 조회/생성/수정/삭제
	
	// 파일 삭제 시도
	
	
	
	
	
	// 사용자가 푼 모든 문제 가져오기
	
	// 사용자 출석 조회/생성/삭제
	
	// 사용자 점수 조회/생성/수정/초기화
	
	// 사용자 성과 조회/생성/수정/초기화
	
	// 사용자 성과 세부 측정 지표 조회/생성/수정/초기화
	
	
	
	
	
	
}
