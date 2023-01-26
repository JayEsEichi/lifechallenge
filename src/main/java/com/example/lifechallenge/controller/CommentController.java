package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.CommentRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc")
@RestController
public class CommentController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/comment/write/{post_id}")
    public ResponseEntity<ResponseBody> commentWrite(HttpServletRequest request, @RequestBody CommentRequestDto commentRequestDto, @PathVariable Long post_id){
        log.info("댓글 작성 - 댓글 작성 유저 : {}, 댓글 작성 내용 일부분 : {}", jwtTokenProvider.getMemberFromAuthentication().getNickname(), commentRequestDto.getContent().substring(0,5));

        return commentService.commentWrite(request, commentRequestDto, post_id);
    }


    // 댓글 수정
    @PutMapping("/comment/update/{comment_id}")
    public ResponseEntity<ResponseBody> commentUpdate(HttpServletRequest request, @RequestBody CommentRequestDto commentRequestDto, @PathVariable Long comment_id){
        log.info("댓글 수정 - 댓글 수정 유저 : {}, 댓글 수정 내용 일부분 : {}", jwtTokenProvider.getMemberFromAuthentication().getNickname(), commentRequestDto.getContent().substring(0,5));

        return commentService.commentUpdate(request, commentRequestDto, comment_id);
    }
}
