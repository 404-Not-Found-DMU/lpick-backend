package com.notfound.lpickbackend.userInfo.config;

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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

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

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청 허용
                .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                .csrf().disable();

        // SecurityFilterChain을 빌드 후 반환
        return http.build();
    }

}
