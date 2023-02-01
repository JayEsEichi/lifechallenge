package com.example.lifechallenge.controller;

import com.example.lifechallenge.controller.request.LoginRequestDto;
import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.request.TestRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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
@RestController
public class MemberController {

    private final MemberService memberService;

    // 회원가입
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
    @PostMapping("/login")
    public ResponseEntity<ResponseBody> memberLogin(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto){
        log.info("로그인 - 아이디 : {}, 비밀번호 : {}", loginRequestDto.getMember_id(), loginRequestDto.getPassword());

        return memberService.memberLogin(response, loginRequestDto);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseBody> memberLogout(HttpServletRequest request){
        log.info("로그아웃 - 유저 액세스 토큰 : {}, 유저 정보 : {}", request.getHeader("Authorization"), request.getUserPrincipal());

        return memberService.memberLogout(request);
    }


    // 회원탈퇴
    @DeleteMapping("/unregister")
    public ResponseEntity<ResponseBody> memberUnregister(HttpServletRequest request){
        log.info("회원탈퇴 - 탈퇴 토큰 : {}", request.getHeader("Authorization"));

        return memberService.memberUnregister(request);
    }


    // api 활용 도로주소 추출 (아직 테스트 중)
    @PostMapping(value="/sample/getAddrApi.do")
    public ResponseEntity<ResponseBody> getAddrApi(@RequestBody TestRequestDto testRequestDto) throws Exception {
        // 요청변수 설정
//        String currentPage = req.getParameter("currentPage");    //요청 변수 설정 (현재 페이지. currentPage : n > 0)
//        String countPerPage = req.getParameter("countPerPage");  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
//        String resultType = req.getParameter("resultType");      //요청 변수 설정 (검색결과형식 설정, json)
//        String confmKey = req.getParameter("confmKey");          //요청 변수 설정 (승인키)
//        String keyword = req.getParameter("keyword");            //요청 변수 설정 (키워드)

        String currentPage = testRequestDto.getCurrentPage();
        String countPerPage = testRequestDto.getCountPerPage();
        String resultType = testRequestDto.getResultType();
        String confmKey = testRequestDto.getConfmKey();
        String keyword = testRequestDto.getKeyword();

        // OPEN API 호출 URL 정보 설정
        String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+ URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String tempStr = null;

        while(true){
            tempStr = br.readLine();
            if(tempStr == null) break;
            sb.append(tempStr);								// 응답결과 JSON 저장
        }
        br.close();

        return new ResponseEntity(new ResponseBody<>(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), sb.toString()), HttpStatus.OK);
    }

}
