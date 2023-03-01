package com.example.lifechallenge.controller.request;

import lombok.Getter;

@Getter
public class ChallengeRequestDto {
    private String placeCategory; // 장소 구분 카테고리 (카페, 편의점 등)
    private int difficulty; // 챌린지 난이도 (쉬움, 보통, 어려움)
    private String placeName; // 장소 이름
    private String placeAddress; // 장소 주소
}
