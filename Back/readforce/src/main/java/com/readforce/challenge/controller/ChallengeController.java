package com.readforce.challenge.controller;

import com.readforce.challenge.dto.ChallengeSubmitResultRequestDto;
import com.readforce.challenge.service.ChallengeService;
import com.readforce.common.MessageCode;
import com.readforce.common.enums.CategoryEnum;
import com.readforce.common.enums.LanguageEnum;
import com.readforce.common.enums.NameEnum;
import com.readforce.common.service.RateLimitingService;
import com.readforce.member.entity.Member;
import com.readforce.member.service.MemberService;
import com.readforce.question.dto.MultipleChoiceResponseDto;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
@Validated
public class ChallengeController {

    private final ChallengeService challengeService;
    private final MemberService memberService;
    private final RateLimitingService rateLimitingService;

    
    @GetMapping("/get-challenge-question-list")
    public ResponseEntity<List<MultipleChoiceResponseDto>> getTodayChallengeQuestionList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("language") @NotNull(message = MessageCode.LANGUAGE_NOT_NULL) LanguageEnum language,
            @RequestParam("category") @NotNull(message = MessageCode.CATEGORY_NOT_NULL) CategoryEnum category
    ) {
        String email = userDetails.getUsername();      
       // rateLimitingService.checkDailyChallengeLimit(email, category, language);       
        List<MultipleChoiceResponseDto> resultList = challengeService.getChallengeQuestionList(language, category);
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }

   
    @PostMapping("/submit-challenge-result")
    public ResponseEntity<Map<String, Double>> submitChallengeResult(
            @RequestBody ChallengeSubmitResultRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        Member member = memberService.getActiveMemberByEmail(email);        
        Double totalScore = challengeService.submitChallengeResult(member, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                NameEnum.SCORE.name(), totalScore
        ));
    }

    
    @PostMapping("/update-to-challenges")
    public ResponseEntity<Map<String, String>> updateToChallengePassages() {
        challengeService.updateToChallengePassages();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                MessageCode.MESSAGE_CODE, MessageCode.UPDATE_CHALLENGE_PASSAGES_SUCCESS
        ));
    }
}
