package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.LoginRequestDto;
import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.request.TokenDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.domain.Token;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.repository.MemberRepository;
import com.example.lifechallenge.repository.TokenRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.lifechallenge.domain.QMember.member;
import static com.example.lifechallenge.domain.QToken.token;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenRepository tokenRepository;

    // 회원가입
    public ResponseEntity<ResponseBody> memberRegister(RegisterRequestDto registerRequestDto) {
        // 아이디 기준으로 이미 존재하는 계정이라면 중복된 계정 에러처리
        if(!(queryFactory
                .selectFrom(member)
                .where(member.member_id.eq(registerRequestDto.getMember_id()))
                .fetchOne() == null)){
            return new ResponseEntity<>(new ResponseBody(StatusCode.DUPLICATE_ACCOUNT.getStatusCode(), StatusCode.DUPLICATE_ACCOUNT.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 비밀번호와 재확인용 비밀번호의 값이 다를 경우 에러처리
        if(!registerRequestDto.getPassword().equals(registerRequestDto.getPassword_recheck())){
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_MATCH_PASSWORD.getStatusCode(), StatusCode.DUPLICATE_ACCOUNT.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 회원가입 정보 build
        Member member = Member.builder()
                .member_id(registerRequestDto.getMember_id())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .nickname(registerRequestDto.getNickname())
                .address(registerRequestDto.getAddress())
                .build();

        // 회원가입
        memberRepository.save(member);

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(),StatusCode.OK.getStatus(), "회원가입이 완료되었습니다."), HttpStatus.OK);
    }


    // 로그인
    @Transactional
    public ResponseEntity<ResponseBody> memberLogin(HttpServletResponse response, LoginRequestDto loginRequestDto){

        log.info("로그인 서비스 진입");

        // 계정이 존재하지 않는지 아이디 기준으로 확인
        if(queryFactory
                .selectFrom(member)
                .where(member.member_id.eq(loginRequestDto.getMember_id()))
                .fetchOne() == null){

            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_ACCOUNT.getStatusCode(), StatusCode.NOT_EXIST_ACCOUNT.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        log.info("로그인할 계정 존재");

        // 아이디가 일치하는 계정이 있다면 해당 계정 불러오기
        Member exist_member = queryFactory
                .selectFrom(member)
                .where(member.member_id.eq(loginRequestDto.getMember_id()))
                .fetchOne();

        // 이미 로그인한 계정이라면 존재했던 토큰 삭제
        if(queryFactory
                .selectFrom(token)
                .where(token.member_id.eq(exist_member.getMember_id()))
                .fetchOne() != null){

            // 존재했던 토큰 삭제
            queryFactory
                    .delete(token)
                    .where(token.member_id.eq(exist_member.getMember_id()))
                    .execute();
        }

        log.info("존재하는 계정 조회");

        // 불러온 계정의 비밀번호와 입력받은 비밀번호가 일치하는지 확인
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), exist_member.getPassword())){
            return new ResponseEntity<>(new ResponseBody(StatusCode.INCORRECT_PASSWORD.getStatusCode(), StatusCode.INCORRECT_PASSWORD.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        log.info("DB에 존재하는 계정과 로그인 요청한 비밀번호 일치 확인");

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getMember_id(), loginRequestDto.getPassword());

        log.info("UsernamePasswordAuthenticationToken 생성되었음을 확인 - {}", authenticationToken);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("Authentication 생성되었음을 확인 - {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성 (우선 Dto로 생성)
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        log.info("TokenDto가 생성되었음을 확인 - {}", tokenDto.getAccessToken());

        // 4. tokenDto에 토큰 정보가 옳바르게 대입되고 난 후에 Response 헤더에 필요한 토큰 정보들을 추가하여 반환
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());

        log.info("Response에 헤더가 추가되고 값이 존재함을 확인 - {}", response.getHeader("Authorization"));

        // 5. Dto로 생성된 token 정보들을 Token 엔티티에 build
        Token token = Token.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .grantType(tokenDto.getGrantType())
                .member_id(exist_member.getMember_id())
                .build();

        log.info("Token에 값이 대입되었음을 확인 - {}", token.getAccessToken());

        // 6. 토큰 저장
        tokenRepository.save(token);

        HashMap<String, String> tokenSet = new HashMap();

        tokenSet.put("grantType", token.getGrantType());
        tokenSet.put("Authoriaztion", token.getGrantType() + " " + token.getAccessToken());
        tokenSet.put("accessToken", token.getAccessToken());
        tokenSet.put("refreshToken", token.getRefreshToken());

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(),  tokenSet), HttpStatus.OK);
    }


    // 로그아웃
    @Transactional
    public ResponseEntity<ResponseBody> memberLogout(HttpServletRequest request){

        // request 에서 액세스토큰 정보 추출
        String refreshToken = request.getHeader("Refresh-Token");

        // 토큰이 유효한지 유효하지 않은지 확인 후 처리
        if(!jwtTokenProvider.validateToken(refreshToken)){
            return new ResponseEntity<>(new ResponseBody(StatusCode.USELESS_TOKEN.getStatusCode(), StatusCode.USELESS_TOKEN.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        if(jwtTokenProvider.getMemberFromAuthentication() == null){
            return new ResponseEntity(new ResponseBody(StatusCode.NOT_EXIST_ACCOUNT.getStatusCode(), StatusCode.NOT_EXIST_ACCOUNT.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 로그아웃 된 계정의 토큰 삭제
        queryFactory
                .delete(token)
                .where(token.refreshToken.eq(refreshToken))
                .execute();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), "로그아웃 되셨습니다."), HttpStatus.OK);
    }
}
