package com.example.lifechallenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class MemberLikePost {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long memberlikepost_id;

    @JsonIgnore
    @JoinColumn(name = "member_pk_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnore
    @JoinColumn(name = "post_pk_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
