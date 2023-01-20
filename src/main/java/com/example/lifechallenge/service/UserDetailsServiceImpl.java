package com.example.lifechallenge.service;

import com.example.lifechallenge.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.lifechallenge.domain.QMember.member;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {

        if (queryFactory
                .selectFrom(member)
                .where(member.member_id.eq(member_id))
                .fetchOne() == null) {

            log.info("loadUserByUsername 실행 - 계정 정보 조회 실패");

            throw new UsernameNotFoundException(member_id + " - 없는 계정정보 입니다.");

        } else {
            Member auth_member = queryFactory
                    .selectFrom(member)
                    .where(member.member_id.eq(member_id))
                    .fetchOne();

            log.info("loadUserByUsername 실행 - 계정 정보 조회 성공");

            return createUserDetails(auth_member);
        }
    }

    private UserDetails createUserDetails(Member member) {

        log.info("createUserDetails 실행");

        return User.builder()
                .username(member.getMember_id())
                .password(member.getPassword())
                .roles(member.getRoles().toArray(new String[0]))
                .build();
    }
}
