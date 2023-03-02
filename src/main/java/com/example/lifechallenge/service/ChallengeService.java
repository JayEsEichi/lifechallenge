package com.example.lifechallenge.service;

import static com.example.lifechallenge.domain.QChallenge.challenge;
import static com.example.lifechallenge.domain.QMemberDoChallenge.memberDoChallenge;

import com.example.lifechallenge.controller.request.ChallengeRequestDto;
import com.example.lifechallenge.controller.response.ChallengeResponseDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.*;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.repository.MemberDoChallengeRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChallengeService {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDoChallengeRepository memberDoChallengeRepository;

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

    // 유저 챌린지 세팅
    public ResponseEntity<ResponseBody> createChallenge(HttpServletRequest request, ChallengeRequestDto challengeRequestDto) throws NullPointerException {

        // 유저 검증
        Member authMember = checkAuthentication(request);

        // 요청한 챌린지 장소 정보, 장소 구분, 난이도로 해당되는 챌린지 정보들을 조회
        List<Challenge> challenges = queryFactory
                .selectFrom(challenge)
                .where(challenge.challengePlaceCategory.eq(challengeRequestDto.getPlaceCategory())
                        .and(challenge.challengeLevel.eq(challengeRequestDto.getDifficulty())))
                .fetch();


        // 유저 챌린지 생성 빌드
        MemberDoChallenge createMemberDoChallenge = MemberDoChallenge.builder()
                .placeCategory(PlaceCategory.inputCategory(challengeRequestDto.getPlaceCategory())) // ENUM 타입의 장소 카테고리
                .difficulty(Difficulty.inputDifficulty(challengeRequestDto.getDifficulty())) // ENUM 타입의 챌린지 난이도
                .placeName(challengeRequestDto.getPlaceName()) // 장소 이름
                .placeAddress(challengeRequestDto.getPlaceAddress()) // 장소 주소
                .member(authMember) // 현재 도전하는 유저
                .challenge(challenges.get((int) (Math.random() * challenges.size()))) // 챌린지 중 랜덤으로 하나 선택
                .build();

        memberDoChallengeRepository.save(createMemberDoChallenge);

        // Entity를 직접 조회해서 가져오는 것이 아닌 성능 개선을 위해 DTO 객체로 조회
        // 생성한 ResponseDto 객체의 필드에 주입 , ResponseDto에 만든 필드명과 동일해야한다.
        // Projections.fields 를 사용할 떄 ResponseDto에 @Builder를 넣으면 수행되지 않는다.
        ChallengeResponseDto challengeResponseDto = queryFactory
                .select(Projections.fields(ChallengeResponseDto.class,
                        memberDoChallenge.member.nickname,
                        memberDoChallenge.challenge.challengePlaceCategory,
                        memberDoChallenge.challenge.challengeCategory,
                        memberDoChallenge.challenge.challengeContent,
                        memberDoChallenge.challenge.challengeLevel,
                        memberDoChallenge.placeName,
                        memberDoChallenge.placeAddress
                ))
                .from(memberDoChallenge)
                .where(memberDoChallenge.memberDoChallengeId.eq(createMemberDoChallenge.getMemberDoChallengeId()))
                .fetchOne();


        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), challengeResponseDto), HttpStatus.OK);
    }
}
