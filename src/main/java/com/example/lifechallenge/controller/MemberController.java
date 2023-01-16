package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc")
@RestController
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ResponseBody> memberRegister(@RequestBody RegisterRequestDto registerRequestDto){
        log.info("회원가입 - 아이디 : {}, 비밀번호 : {}, 재확인 비밀번호 : {}, 닉네임 : {}, 주소 : {}",
                registerRequestDto.getMember_id(),
                registerRequestDto.getPassword(),
                registerRequestDto.getPassword_recheck(),
                registerRequestDto.getNickname(),
                registerRequestDto.getAddress());

        return memberService.memberRegister(registerRequestDto);
    }

}
