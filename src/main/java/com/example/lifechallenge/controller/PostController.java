package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.PostRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc")
@Controller
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/post/write")
    public ResponseEntity<ResponseBody> postWrite(HttpServletRequest request, @RequestBody PostRequestDto postRequestDto){
        log.info("게시글 작성 - 제목 : {}, 내용 일부분 : {}", postRequestDto.getTitle(), postRequestDto.getContent().substring(0, postRequestDto.getContent().length()/2));

        return postService.postWrite(request, postRequestDto);
    }

    // 게시글 수정
    @PutMapping("/post/update/{post_id}")
    public ResponseEntity<ResponseBody> postUpdate(HttpServletRequest request, @RequestBody PostRequestDto postRequestDto, @PathVariable Long post_id){
        log.info("게시글 수정 - 수정 제목 : {}, 수정 내용 : {}, 수정 게시글 id : {}" , postRequestDto.getTitle(), postRequestDto.getContent(), post_id);

        return postService.postUpdate(request, postRequestDto, post_id);
    }

}
