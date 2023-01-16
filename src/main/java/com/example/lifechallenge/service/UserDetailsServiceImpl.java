package com.example.lifechallenge.service;

import com.example.lifechallenge.domain.Member;
import com.example.lifechallenge.domain.UserDetailsImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.lifechallenge.domain.QMember.member;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JPAQueryFactory queryFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {

        if (queryFactory
                .selectFrom(member)
                .where(member.member_id.eq(member_id))
                .fetchOne() == null) {

            throw new UsernameNotFoundException(member_id + " - 없는 계정정보 입니다.");

        } else {
            Member auth_member = queryFactory
                    .selectFrom(member)
                    .where(member.member_id.eq(member_id))
                    .fetchOne();

            return createUserDetails(auth_member);
        }
    }

    private UserDetails createUserDetails(Member member) {
        return UserDetailsImpl.builder()
                .member_id(member.getMember_id())
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(List.of(member.getRoles().toArray(new String[0])))
                .build();
    }
}
