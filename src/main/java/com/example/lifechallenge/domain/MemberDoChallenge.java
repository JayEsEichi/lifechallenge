package com.example.lifechallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class MemberDoChallenge {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long memberDoChallengeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory placeCategory; // Enum 타입 장소 타입

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty; // Enum 타입 챌린지 난이도

    @Column(nullable = false)
    private String placeName; // 장소 이름

    @Column(nullable = false)
    private String placeAddress; // 장소 주소

    @OneToOne
    @JoinColumn(name = "member_pk_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "challenge_pk_id")
    private Challenge challenge;
}
