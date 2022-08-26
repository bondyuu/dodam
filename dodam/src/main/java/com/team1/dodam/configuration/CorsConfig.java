package com.team1.dodam.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    // CORS 정책에서 벗어나기 위한 구성

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true); // 내 서버가 응답할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것. false로 하면 자바스크립트로 요청했을때 오지 않음.
        configuration.addAllowedOriginPattern("*"); //모든 ip로부터 접속 허용.
        configuration.addAllowedHeader("*"); //모든 header 접속 허용.
        configuration.addAllowedMethod("*"); //모든 HTTP Method, get, post, put 등등 접속 허용.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
