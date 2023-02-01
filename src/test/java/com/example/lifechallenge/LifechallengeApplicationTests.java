package com.example.lifechallenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LifechallengeApplicationTests {

    @Test
    void contextLoads() {
        StringBuffer sb = new StringBuffer("test");
        System.out.println(sb + " " + System.identityHashCode(sb)); //문자열과 메모리 주소 출력

        sb.append("2"); // 문자열 추가
        System.out.println(sb + " " + System.identityHashCode(sb)); //문자열과 메모리 주소 출력
    }



}
