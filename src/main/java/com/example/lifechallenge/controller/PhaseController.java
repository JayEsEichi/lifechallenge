package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.service.PhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc")
@RestController
public class PhaseController {

    private final PhaseService phaseService;
    private final JwtTokenProvider jwtTokenProvider;

    // 1단계 실행 (난이도)
    @PostMapping("/phase1")
    public ResponseEntity<ResponseBody> activatePhase1(HttpServletRequest request, @RequestParam String level){
        log.info("페이즈 1단계 - 라이프 첼린지를 시작할 유저 : {}, 난이도 : {}", jwtTokenProvider.getMemberFromAuthentication(), level);

        return phaseService.activatePhase1(request, level);
    }
}
