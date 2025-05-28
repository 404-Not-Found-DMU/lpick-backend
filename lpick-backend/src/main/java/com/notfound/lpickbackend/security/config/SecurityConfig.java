package com.notfound.lpickbackend.security.config;

import com.notfound.lpickbackend.security.handler.CustomOAuth2SuccessHandler;
import com.notfound.lpickbackend.userinfo.command.application.service.OAuth2UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserCommandService oAuth2UserCommandService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(config -> config.anyRequest().permitAll()
        )
                .formLogin(config -> config.disable()) // 폼 로그인 비활성화
                .httpBasic(config -> config.disable()) // HTTP Basic 인증 비활성화
        ;
        http.oauth2Login(oauth2Configurer -> oauth2Configurer
                .successHandler(customOAuth2SuccessHandler)
//                .userInfoEndpoint()
//                .userService(oAuth2UserCommandService)
        );

        return http.build();
    }

}
