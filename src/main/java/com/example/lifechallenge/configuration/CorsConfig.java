package com.example.lifechallenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080",
                "http://dapi.kakao.com/v2/maps/sdk.js?appkey=195dc7ba59bfaecca5efb89e073cce95",
                "https://dapi.kakao.com/v2/maps/sdk.js?appkey=195dc7ba59bfaecca5efb89e073cce95",
                "https://t1.daumcdn.net/mapjsapi/js/libs/drawing/1.2.6/drawing.js",
                "https://t1.daumcdn.net/mapjsapi/js/libs/clusterer/1.0.9/clusterer.js",
                "https://t1.daumcdn.net/mapjsapi/**"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
