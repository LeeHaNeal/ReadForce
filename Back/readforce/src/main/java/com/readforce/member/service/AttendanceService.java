package com.readforce.member.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.DuplicationException;
import com.readforce.member.entity.Attendance;
import com.readforce.member.entity.Member;
import com.readforce.member.repository.AttendanceRepository;
import com.readforce.result.service.ResultService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	private final ResultService resultService;
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

	@Transactional(readOnly = true)
	public List<LocalDate> getAttendanceDateList(String email) {

		return attendanceRepository
				.findAllByMember_Email(email)
				.stream()
				.map(attendance -> attendance.getAttendanceDate())
				.collect(Collectors.toList());

	}

}
