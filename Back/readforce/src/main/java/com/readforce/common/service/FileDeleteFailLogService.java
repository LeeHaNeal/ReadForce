package com.readforce.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.entity.FileDeleteFailLog;
import com.readforce.common.repository.FileDeleteFailLogRepository;
import com.readforce.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDeleteFailLogService {

	private final FileDeleteFailLogRepository fileDeleteFailLogRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Member member, String message) {

		FileDeleteFailLog fileDelteFailLog = FileDeleteFailLog.builder()
				.member(member)
				.filePath(member.getProfileImagePath())
				.reason(message)
				.build();
		
		fileDeleteFailLogRepository.save(fileDelteFailLog);
		
	}
	
}
