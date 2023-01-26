package com.example.lifechallenge.controller.response;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class CommentResponseDto {
    private String content;
    private String nickname;
    private String createdAt;
    private String modifiedAt;
}

