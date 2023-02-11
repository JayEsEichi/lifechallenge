package com.example.lifechallenge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 메인 페이지 이동
    @GetMapping("/home")
    public String moveHomePage(){

        return "home";
    }

    // 로그인 페이지 이동
    @GetMapping("/login")
    public String moveLoginPage(){

        return "login";
    }

    // 회원가입 페이지 이동
    @GetMapping("/regist")
    public String moveRegisterPage(){

        return "register";
    }

    // 게시판 페이지 이동
    @GetMapping("/post")
    public String movePostPage(){

        return "post";
    }

    // 게시판 페이지 이동
    @GetMapping("/chat")
    public String moveChatPage(){

        return "chat";
    }
}
