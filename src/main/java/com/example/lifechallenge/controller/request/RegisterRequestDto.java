package com.example.lifechallenge.controller.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {
    private String member_id;
    private String password;
    private String password_recheck;
    private String nickname;
    private String address;
}
