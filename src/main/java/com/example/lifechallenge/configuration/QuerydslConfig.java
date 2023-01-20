package com.example.lifechallenge.configuration;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em){
        log.info("회원관리 기능 절차(jwt) -> QuerydslConfig - jpaQueryFactory 메소드 (EntityManager : {})", em);
        return new JPAQueryFactory(em);
    }
}