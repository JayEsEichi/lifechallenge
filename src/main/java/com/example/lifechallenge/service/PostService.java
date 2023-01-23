package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.PostRequestDto;

import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.domain.Post;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static com.example.lifechallenge.domain.QPost.post;

@RequiredArgsConstructor
@Service
public class PostService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory queryFactory;
    private final PostRepository postRepository;

    // 발급된 토큰 및 계정 유효성 검증
    private Member checkAuthentication(HttpServletRequest request){

        // 리프레시 토큰 유효성 검사
        if(!jwtTokenProvider.validateToken(request.getHeader("Refresh-Token"))){
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // Authentication 유효성 검사
        if(jwtTokenProvider.getMemberFromAuthentication() == null){
            throw new RuntimeException("존재하지 않는 계정입니다.");
        }

        Member member = jwtTokenProvider.getMemberFromAuthentication();

        return member;
    }


    // 게시글 작성
    public ResponseEntity<ResponseBody> postWrite(HttpServletRequest request, PostRequestDto postRequestDto){

        // 게시글 작성 유저 정보 불러오기
        Member auth_member = checkAuthentication(request);

        if(postRequestDto.getTitle() == null || postRequestDto.getContent() == null){
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_POST_INFO.getStatusCode(),  StatusCode.NOT_EXIST_POST_INFO.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 게시글 내용 정보들 build
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .viewcnt(0)
                .likecnt(0)
                .member(auth_member)
                .build();

        // 게시글 저장
        postRepository.save(post);

        // 게시글 작성 시 원하는 정보들이 출력될 수 있도록 HashMap으로 형식을 잡아 출력
        HashMap<String, String> postInfoSet = new HashMap<>();

        postInfoSet.put("title", post.getTitle()); // 제목
        postInfoSet.put("content", post.getContent()); // 내용
        postInfoSet.put("viewcnt", post.getViewcnt().toString()); // 조회수
        postInfoSet.put("likecnt", post.getLikecnt().toString()); // 좋아요 수
        postInfoSet.put("nickname", post.getMember().getNickname()); // 게시글 작성자 닉네임
        postInfoSet.put("createdAt", post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 게시글 작성일자
        postInfoSet.put("modifiedAt", post.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 게시글 수정일자

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(),  StatusCode.OK.getStatus(), postInfoSet), HttpStatus.OK);
    }
}
