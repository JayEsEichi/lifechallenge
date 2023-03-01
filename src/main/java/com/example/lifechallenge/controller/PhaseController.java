package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.ChallengeRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.service.PhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc/challenge")
@RestController
public class PhaseController {

    private final PhaseService phaseService;
    private final JwtTokenProvider jwtTokenProvider;

    // 요청된 정보들로 유저 챌린지 세팅
    @PostMapping("/create")
    public ResponseEntity<ResponseBody> createChallenge(HttpServletRequest request, @RequestBody ChallengeRequestDto challengeRequestDto){
        log.info("페이즈 1단계 - 라이프 첼린지를 시작할 유저 : {}, 난이도 : {}", jwtTokenProvider.getMemberFromAuthentication(), challengeRequestDto.getDifficulty());

        return phaseService.createChallenge(request, challengeRequestDto);
    }


}
