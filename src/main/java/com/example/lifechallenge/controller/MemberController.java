package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.LoginRequestDto;
import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lc")
@Controller
public class MemberController {

    private final MemberService memberService;

    @Value("${address.api.confirm.key}")
    private String address_key;

    // 회원가입
    @org.springframework.web.bind.annotation.ResponseBody
    @PostMapping("/register")
    public ResponseEntity<ResponseBody> memberRegister(@RequestBody RegisterRequestDto registerRequestDto){
        log.info("회원가입 - 아이디 : {}, 비밀번호 : {}, 재확인 비밀번호 : {}, 닉네임 : {}, 주소 : {}",
                registerRequestDto.getMember_id(),
                registerRequestDto.getPassword(),
                registerRequestDto.getPassword_recheck(),
                registerRequestDto.getNickname(),
                registerRequestDto.getAddress());

        return memberService.memberRegister(registerRequestDto);
    }


    // 로그인
    @org.springframework.web.bind.annotation.ResponseBody
    @PostMapping("/login")
    public ResponseEntity<ResponseBody> memberLogin(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto){
        log.info("로그인 - 아이디 : {}, 비밀번호 : {}", loginRequestDto.getMember_id(), loginRequestDto.getPassword());

        return memberService.memberLogin(response, loginRequestDto);
    }


    // 로그아웃
    @org.springframework.web.bind.annotation.ResponseBody
    @PostMapping("/logout")
    public ResponseEntity<ResponseBody> memberLogout(HttpServletRequest request){
        log.info("로그아웃 - 유저 액세스 토큰 : {}, 유저 정보 : {}", request.getHeader("Authorization"), request.getUserPrincipal());

        return memberService.memberLogout(request);
    }


    // 회원탈퇴
    @org.springframework.web.bind.annotation.ResponseBody
    @DeleteMapping("/unregister")
    public ResponseEntity<ResponseBody> memberUnregister(HttpServletRequest request){
        log.info("회원탈퇴 - 탈퇴 토큰 : {}", request.getHeader("Authorization"));

        return memberService.memberUnregister(request);
    }


    // api 활용 도로주소 추출 (아직 테스트 중)
    @PostMapping(value="/sample/getAddrApi.do")
    public void getAddrApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 요청변수 설정
        String currentPage = request.getParameter("currentPage");    //요청 변수 설정 (현재 페이지. currentPage : n > 0)
        String countPerPage = request.getParameter("countPerPage");  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
        String resultType = request.getParameter("resultType");      //요청 변수 설정 (검색결과형식 설정, json)
        String keyword = request.getParameter("keyword");            //요청 변수 설정 (키워드)

        log.info("현재 페이지 : {}, 페이지 당 출력 개수 : {}, 검색 결과 형식 : {}, 검색 키워드 : {}", currentPage, countPerPage, resultType, keyword);

        // OPEN API 호출 URL 정보 설정
        String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+ URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+address_key+"&resultType="+resultType;
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String tempStr = null;

        while(true){
            tempStr = br.readLine();
            if(tempStr == null) break;
            sb.append(tempStr);
        }

        br.close();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        response.getWriter().write(sb.toString());			// 응답결과 반환

//        return new ResponseEntity(new ResponseBody<>(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), sb.toString()), HttpStatus.OK);
    }

}
