package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.PostRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
