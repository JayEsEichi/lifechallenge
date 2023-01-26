package com.example.lifechallenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LifechallengeApplicationTests {

    @Test
    void contextLoads() {
        String str1 = new String("test");
        String str2 = new String("test");

        String s1 = "testing";
        String s2 = "testing";

        System.out.println(s1.equals(s2));
    }



}
