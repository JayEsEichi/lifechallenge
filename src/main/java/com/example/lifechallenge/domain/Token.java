package com.example.lifechallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long token_pk_id;

    @Column
    private String grantType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;
}
