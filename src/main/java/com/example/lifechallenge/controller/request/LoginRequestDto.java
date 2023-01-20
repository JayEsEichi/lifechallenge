package com.example.lifechallenge.controller.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String member_id;
    private String password;
}
