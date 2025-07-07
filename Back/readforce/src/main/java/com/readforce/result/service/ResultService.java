package com.readforce.result.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readforce.common.MessageCode;
import com.readforce.common.exception.ResourceNotFoundException;
import com.readforce.member.entity.Member;
import com.readforce.result.entity.Result;
import com.readforce.result.repository.ResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    @Transactional
    public void create(Member member) {
        Result result = Result.builder()
                .member(member)
                .build();

        resultRepository.save(result);
    }

    @Transactional(readOnly = true)
    public Result getActiveMemberResult(String email) {
        return resultRepository.findByMember_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageCode.MEMBER_RESULT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Integer getLearningStreak(String email) {
        Result result = getActiveMemberResult(email);
        return result.getLearningStreak();
    }

    /**
     * 결과가 없으면 생성해서 반환하는 메서드
     */
    @Transactional
    public Result getOrCreateActiveMemberResult(String email, Member member) {
        return resultRepository.findByMember_Email(email)
                .orElseGet(() -> {
                    create(member);
                    return resultRepository.findByMember_Email(email)
                            .orElseThrow(() -> new ResourceNotFoundException(MessageCode.MEMBER_RESULT_NOT_FOUND));
                });
    }
}
