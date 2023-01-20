package com.example.lifechallenge.controller.request;

import lombok.Builder;
import lombok.Getter;
import java.util.Date;

@Getter
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiresIn;
}
