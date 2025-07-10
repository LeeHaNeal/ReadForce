package com.readforce.member.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.Attendance;
import com.readforce.member.entity.Member;
import com.readforce.member.repository.AttendanceRepository;
import com.readforce.result.entity.Result;
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

       
        if (!attendanceRepository.findByMember_EmailAndAttendanceDate(email, LocalDate.now()).isEmpty()) {
            return;
        }

        Member member = memberService.getActiveMemberByEmail(email);

        Attendance attendance = Attendance.builder()
                .member(member)
                .attendanceDate(LocalDate.now())
                .build();

        attendanceRepository.save(attendance);

        updateLearningStreak(email);
    }

    private void updateLearningStreak(String email) {

        Result result = resultService.getActiveMemberResultByEmail(email);

        LocalDate yesterday = LocalDate.now().minusDays(1);

        
        boolean attendedYesterday = !attendanceRepository
                .findByMember_EmailAndAttendanceDate(email, yesterday)
                .isEmpty();

        result.updateLearningStreak(attendedYesterday);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getAttendanceDateList(String email) {

        List<Attendance> attendanceList = attendanceRepository.findAllByMember_Email(email);

        if (attendanceList.isEmpty()) {
            throw new ResourceNotFoundException(MessageCode.ATTENDANCE_NOT_FOUND);
        }

        return attendanceList
                .stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toList());
    }

}
