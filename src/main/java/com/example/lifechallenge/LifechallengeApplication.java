package com.example.lifechallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LifechallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifechallengeApplication.class, args);
        System.out.println("어플리케이션 실행~~~!!");
    }

}
