package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    // 회원가입
    public ResponseEntity<ResponseBody> memberRegister(RegisterRequestDto registerRequestDto){
        return new ResponseEntity<>(new ResponseBody(Status.OK.,"",""));
    }
}
