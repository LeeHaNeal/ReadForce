package com.readforce.member.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readforce.member.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


    List<Attendance> findByMember_EmailAndAttendanceDate(String email, LocalDate attendanceDate);

    List<Attendance> findAllByMember_Email(String email);
}
