package com.notfound.lpickbackend.security.config;

import com.notfound.lpickbackend.security.filter.JwtFilter;
import com.notfound.lpickbackend.security.handler.CustomOAuth2SuccessHandler;
import com.notfound.lpickbackend.security.service.CustomOAuth2UserService;
import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(config -> config.anyRequest().permitAll()
        )
                .formLogin(config -> config.disable()) // 폼 로그인 비활성화
                .httpBasic(config -> config.disable()) // HTTP Basic 인증 비활성화
        ;
        http.oauth2Login(oauth2Config -> oauth2Config
                .userInfoEndpoint(userInfoConfig -> userInfoConfig.userService(customOAuth2UserService))
                .successHandler(customOAuth2SuccessHandler)
        );

        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
