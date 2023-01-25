package com.example.lifechallenge.controller.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostResponseDto {
    private String title;
    private String content;
    private String nickname;
    private String createdAt;
    private Integer viewcnt;
    private Integer likecnt;
}
