package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.RegisterRequestDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.lifechallenge.domain.QMember.member;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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


}
