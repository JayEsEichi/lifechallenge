package com.example.lifechallenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Comment extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long comment_id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String nickname;

    @JsonIgnore
    @JoinColumn(name = "post_pk_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

}
