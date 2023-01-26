package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.CommentRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Comment;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.domain.Post;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.repository.CommentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static com.example.lifechallenge.domain.QPost.post;
import static com.example.lifechallenge.domain.QComment.comment;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory queryFactory;
    private final CommentRepository commentRepository;

    // 발급된 토큰 및 계정 유효성 검증
    private Member checkAuthentication(HttpServletRequest request) {

        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // Authentication 유효성 검사
        if (jwtTokenProvider.getMemberFromAuthentication() == null) {
            throw new RuntimeException("존재하지 않는 계정입니다.");
        }

        Member member = jwtTokenProvider.getMemberFromAuthentication();

        return member;
    }


    // 댓글 작성
    public ResponseEntity<ResponseBody> commentWrite(HttpServletRequest request, CommentRequestDto commentRequestDto, Long post_id){

        // 유저 검증
        Member auth_member = checkAuthentication(request);

        // 댓글을 작성하고자하는 게시글 조회
        Post write_on_post = queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id))
                .fetchOne();

        // 댓글 작성 정보 builder로 대입
        Comment write_comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .nickname(auth_member.getNickname())
                .post(write_on_post)
                .build();

        // 댓글 작성 정보 저장
        commentRepository.save(write_comment);

        // 작성된 댓글 http 반환 양식
        HashMap<String, String> commentSet = new HashMap<>();

        commentSet.put("content", write_comment.getContent()); // 댓글 내용
        commentSet.put("nickname", write_comment.getNickname()); // 댓글 작성자 닉네임
        commentSet.put("post", write_comment.getPost().getPost_id().toString()); // 댓글이 작성된 게시글의 id
        commentSet.put("createdAt", write_comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 댓글 생성일자
        commentSet.put("modifiedAt", write_comment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 댓글 수정일자

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), commentSet), HttpStatus.OK);
    }
}
