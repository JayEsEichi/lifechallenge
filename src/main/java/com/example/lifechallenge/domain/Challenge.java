package com.example.lifechallenge.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Challenge {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long challenge_pk_id;

    @Column(nullable = false)
    private String challengePlaceCategory; // 챌린지 진행 장소 구분 (카페, 편의점 등)

    @Column(nullable = false)
    private String challengeCategory; // 첼린지 카테고리 (예 : 구매, 방문, 활동, 운동 등등)

    @Column(nullable = false)
    private String challengeContent; // 첼린지 내용 (예 : 구매 도전과제의 경우 물품을 몇 개를 구매한다.)

    @Column(nullable = false)
    private int challengeLevel; // 첼린지 난이도

    @Column(nullable = false)
    private Integer point; // 챌린지 내용과 난이도에 따른 부여 포인트

    @OneToOne(mappedBy = "challenge")
    private MemberDoChallenge memberDoChallenge;
}
