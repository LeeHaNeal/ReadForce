package com.readforce.member.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.DuplicationException;
import com.readforce.member.entity.Attendance;
import com.readforce.member.entity.Member;
import com.readforce.member.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	
	private final MemberService memberService;
	
	@Transactional
	public void recordAttendance(String email) {

		if(attendanceRepository.findByMember_EmailAndAttendanceDate(email, LocalDate.now()).isPresent()) {
			
			throw new DuplicationException(MessageCode.TODAY_ALREADY_ATTENDANCE);
			
		}
		
		Member member = memberService.getActiveMemberByEmail(email);
		
		Attendance attendance = Attendance.builder()
				.member(member)
				.attendanceDate(LocalDate.now())
				.build();
		
		attendanceRepository.save(attendance);		
		
	}

}
