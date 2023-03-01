package com.example.lifechallenge.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChallengeResponseDto {

    private String nickname;
    private String challengePlaceCategory;
    private String challengeCategory;
    private String challengeContent;
    private int challengeLevel;
    private String placeName;
    private String placeAddress;
}
