package com.example.lifechallenge.service;

import com.example.lifechallenge.controller.request.PostRequestDto;

import com.example.lifechallenge.controller.response.PostResponseDto;
import com.example.lifechallenge.controller.response.ResponseBody;
import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.domain.MemberLikePost;
import com.example.lifechallenge.domain.Post;
import com.example.lifechallenge.exception.StatusCode;
import com.example.lifechallenge.jwt.JwtTokenProvider;
import com.example.lifechallenge.repository.MemberLIkePostRepository;
import com.example.lifechallenge.repository.PostRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.lifechallenge.domain.QPost.post;
import static com.example.lifechallenge.domain.QMemberLikePost.memberLikePost;

@RequiredArgsConstructor
@Service
public class PostService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory queryFactory;
    private final PostRepository postRepository;
    private final MemberLIkePostRepository memberLIkePostRepository;
    private final EntityManager entityManager;

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


    // 게시글 작성
    public ResponseEntity<ResponseBody> postWrite(HttpServletRequest request, PostRequestDto postRequestDto) {

        // 게시글 작성 유저 정보 불러오기
        Member auth_member = checkAuthentication(request);

        // 게시글 작성 정보는 null일 수 없음.
        if (postRequestDto.getTitle() == null || postRequestDto.getContent() == null) {
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_POST_INFO.getStatusCode(), StatusCode.NOT_EXIST_POST_INFO.getStatus(), null), HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), postInfoSet), HttpStatus.OK);
    }


    // 게시글 수정
    @Transactional
    public ResponseEntity<ResponseBody> postUpdate(HttpServletRequest request, PostRequestDto postRequestDto, Long post_id) {

        // 게시글 작성 유저 정보 불러오기
        Member auth_member = checkAuthentication(request);

        // 게시글 작성 정보는 null일 수 없음
        if (postRequestDto.getTitle() == null || postRequestDto.getContent() == null) {
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_POST_INFO.getStatusCode(), StatusCode.NOT_EXIST_POST_INFO.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 수정하고자 하는 유저가 해당 게시글을 작성한 유저가 맞는지 확인
        if (queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id).and(post.member.eq(auth_member)))
                .fetchOne() == null) {
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_MATCH_POST_WRITER.getStatusCode(), StatusCode.NOT_MATCH_POST_WRITER.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 게시글 수정
        queryFactory
                .update(post)
                .set(post.title, postRequestDto.getTitle()) // 게시글 제목 수정
                .set(post.content, postRequestDto.getContent()) // 게시글 내용 수정
                .set(post.modifiedAt, LocalDateTime.now()) // 게시글 수정일자 업데이트
                .where(post.post_id.eq(post_id)) // 수정하고자하는 게시글의 id
                .execute();


        entityManager.flush(); // DB에 반영
        entityManager.clear(); // 캐시에 남아있는 정보 비워냄

        // 업데이트한 게시글 정보 조회
        Post update_post = queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id))
                .fetchOne();

        // 게시글 수장 완료 후 게시글 정보들 HashMap으로 출력
        HashMap<String, String> updatePostInfoSet = new HashMap<>();

        updatePostInfoSet.put("title", update_post.getTitle()); // 제목
        updatePostInfoSet.put("content", update_post.getContent()); // 내용
        updatePostInfoSet.put("viewcnt", update_post.getViewcnt().toString()); // 조회수
        updatePostInfoSet.put("likecnt", update_post.getLikecnt().toString()); // 좋아요 수
        updatePostInfoSet.put("nickname", update_post.getMember().getNickname()); // 게시글 작성자 닉네임
        updatePostInfoSet.put("createdAt", update_post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 게시글 작성일자
        updatePostInfoSet.put("modifiedAt", update_post.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 게시글 수정일자


        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), updatePostInfoSet), HttpStatus.OK);
    }


    // 게시글 삭제
    @Transactional
    public ResponseEntity<ResponseBody> postDelete(HttpServletRequest request, Long post_id) {

        // 게시글 작성 유저 정보 불러오기
        Member auth_member = checkAuthentication(request);

        // 삭제하고자 하는 유저가 게시글의 작성자인지 확인
        if (queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id).and(post.member.eq(auth_member)))
                .fetchOne() == null) {
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_MATCH_POST_WRITER.getStatusCode(), StatusCode.NOT_MATCH_POST_WRITER.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 게시글 삭제
        queryFactory
                .delete(post)
                .where(post.post_id.eq(post_id))
                .execute();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), "게시글 정상적으로 삭제되었습니다."), HttpStatus.OK);
    }


    // 게시글 조회
    @Transactional
    public ResponseEntity<ResponseBody> postRead(HttpServletRequest request, Long post_id) {

        // 유저 검증
        Member auth_member = checkAuthentication(request);

        // 조회하고자 하는 게시글 조회
        if (queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id))
                .fetchOne() == null) {
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_EXIST_POST_INFO.getStatusCode(), StatusCode.NOT_EXIST_POST_INFO.getStatus(), null), HttpStatus.BAD_REQUEST);
        }

        // 게시글 정보 불러오기
        Post read_post = queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id))
                .fetchOne();

        // 게시글 조회 수 업데이트
        queryFactory
                .update(post)
                .set(post.viewcnt, read_post.getViewcnt() + 1)
                .where(post.post_id.eq(post_id))
                .execute();

        entityManager.flush();
        entityManager.clear();


        // 조회하고자 하는 게시글의 정보들
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .title(read_post.getTitle()) // 게시글 제목
                .content(read_post.getContent()) // 게시글 내용
                .nickname(read_post.getMember().getNickname()) // 게시글 작성자
                .createdAt(read_post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))) // 게시글 생성일자
                .viewcnt(read_post.getViewcnt()) // 게시글 조회 수
                .likecnt(read_post.getLikecnt()) // 게시글 좋아요 수
                .build();


        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), postResponseDto), HttpStatus.OK);
    }


    // 게시글 전체 목록 조회
    public ResponseEntity<ResponseBody> postReadList(HttpServletRequest request) {

        // 유저 검증
        Member auth_member = checkAuthentication(request);

        // 작성된 모든 게시글 리스트화
        List<Post> postList = queryFactory
                .selectFrom(post)
                .fetch();

        // 최종적으로 리스트화된 게시글들이 반환될 List
        List<HashMap<String, String>> postListSet = new ArrayList<>();

        // 전체 게시글들을 하나씩 조회
        for(Post each_post : postList){

            // HashMap으로 조회된 게시글 일부 정보가 담김
            HashMap<String, String> postSet = new HashMap<>();

            postSet.put("post_id", each_post.getPost_id().toString()); // 게시글 id
            postSet.put("title", each_post.getTitle()); // 게시글 제목
            postSet.put("nickname", each_post.getMember().getNickname()); // 게시글 작성자 닉네임
            postSet.put("viewcnt", each_post.getViewcnt().toString()); // 게시글 조회수
            postSet.put("createdAt", each_post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm"))); // 게시글 생성일자

            // 최종 반환 리스트에 담음
            postListSet.add(postSet);

        }

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), postListSet), HttpStatus.OK);
    }


    // 게시글 좋아요
    @Transactional
    public ResponseEntity<ResponseBody> postLike(HttpServletRequest request, Long post_id){

        // 유저 검증
        Member auth_member = checkAuthentication(request);

        // 좋아요할 게시글 조회
        Post like_post = queryFactory
                .selectFrom(post)
                .where(post.post_id.eq(post_id))
                .fetchOne();

        // 좋아요가 되었는지 좋아요가 취소되었는지 알기위한 알림 문구
        String notice = "";

        // 좋아요할 게시글에 이미 좋아요 처리가 되어있는지 확인
        if(queryFactory
                .selectFrom(memberLikePost)
                .where(memberLikePost.member.eq(auth_member).and(memberLikePost.post.eq(like_post)))
                .fetchOne() != null){
            // 좋아요가 이미 되어있을 경우 MemberLikePost 에서 좋아요 삭제처리
            queryFactory
                    .delete(memberLikePost)
                    .where(memberLikePost.member.eq(auth_member).and(memberLikePost.post.eq(like_post)))
                    .execute();

            // 좋아요 취소 시 notice에 문구 반영
            notice = "좋아요 취소";
        }else{
            // 유저가 게시글에 좋아요한 이력 저장
            MemberLikePost likepost = MemberLikePost.builder()
                    .post(like_post)
                    .member(auth_member)
                    .build();

            memberLIkePostRepository.save(likepost);

            // 좋아요 시 notica에 문구 반영
            notice = "좋아요";
        }

        // 해당 게시글좋아요 수
        Long likeCnt = queryFactory
                .select(memberLikePost.count())
                .from(memberLikePost)
                .where(memberLikePost.post.eq(like_post))
                .fetchOne();

        // 게시글 likecnt 정보 업데이트
        queryFactory
                .update(post)
                .set(post.likecnt, Integer.parseInt(likeCnt.toString()))
                .where(post.post_id.eq(like_post.getPost_id()))
                .execute();

        return new ResponseEntity<>(new ResponseBody<>(StatusCode.OK.getStatusCode(), StatusCode.OK.getStatus(), notice), HttpStatus.OK);
    }
}
