package com.notfound.lpickbackend.security.config;

import com.notfound.lpickbackend.security.filter.JwtFilter;
import com.notfound.lpickbackend.security.handler.CustomOAuth2SuccessHandler;
import com.notfound.lpickbackend.security.service.CustomOAuth2UserService;
import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors
                .configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(config -> config
                        .requestMatchers("/login", "/api/v1/developer-token","/swagger-ui.html/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll() // 개발자용 토큰 요청 허용
                        .anyRequest().authenticated() // 테스트를 위해 임시로 설정
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

    // 1) Tier만을 위한 RoleHierarchy 빈
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl h = new RoleHierarchyImpl();
        h.setHierarchy(
                // hasRole로 비교할 Tier 계층구조
                "ROLE_TIER_ADMIN > ROLE_TIER_EXPERT\n" +
                        "ROLE_TIER_EXPERT > ROLE_TIER_DIAMOND\n" +
                        "ROLE_TIER_CHALLENGER > ROLE_TIER_DIAMOND\n" +
                        "ROLE_TIER_DIAMOND > ROLE_TIER_GOLD\n" +
                        "ROLE_TIER_GOLD > ROLE_TIER_SILVER\n" +
                        "ROLE_TIER_SILVER > ROLE_TIER_BRONZE" +
                        // hasAuthority로 비교할 Auth 계층구조(ADMIN이 모든 기능 가능하게 하기위함.
                        "AUTH_ADMIN > AUTH_MANAGER\n" +
                        "AUTH_ADMIN > AUTH_MEDIATOR"
        );
        return h;
    }

    // 2) Method Security Expression Handler에 위 계층 주입
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy hierarchy) {
        DefaultMethodSecurityExpressionHandler h = new DefaultMethodSecurityExpressionHandler();
        h.setRoleHierarchy(hierarchy);
        return h;
    }

    // 3) CORS 빈 생성
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("https://lpick.duckdns.org"); // nginx 도메인
        configuration.addAllowedOrigin("https://lpick-frontend-deploy-2pom.vercel.app");
        configuration.addAllowedOrigin("http://localhost:3000"); // 프론트 로컬 도메인
        configuration.addAllowedOrigin("http://localhost:8080"); // 프론트 로컬 도메인
        configuration.addAllowedOrigin("https://lpick.in");


        configuration.addAllowedOriginPattern("https://*.lpick.in");
        configuration.addAllowedOriginPattern("https://lpick.duckdns.org"); // nginx 도메인
        configuration.addAllowedOriginPattern("https://lpick-frontend-deploy-2pom.vercel.app");
        configuration.addAllowedOriginPattern("http://localhost:3000"); // 프론트 로컬 도메인
        configuration.addAllowedOriginPattern("http://localhost:8080"); // 프론트 로컬 도메인

        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 인증 필요하면 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
