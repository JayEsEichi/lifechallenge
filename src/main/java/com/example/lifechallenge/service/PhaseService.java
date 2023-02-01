package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class PhaseService {

    private final EntityManager entityManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 발급된 토큰 및 계정 유효성 검증
    private Member checkAuthentication(HttpServletRequest request) {

        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // Authentication 유효성 검사
        if (jwtTokenProvider.getMemberFromAuthentication() == null) {
            throw new RuntimeException("존재하지 않는 계정입니다.");
        }

        Member member = jwtTokenProvider.getMemberFromAuthentication();

        return member;
    }

    // 1단계 실행 (난이도)
    public ResponseEntity<ResponseBody> activatePhase1(HttpServletRequest request, String level){

        // 유저 검증
        Member auth_member = checkAuthentication(request);

        ArrayList arl = new ArrayList();



        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), ""), HttpStatus.OK);
    }
}
