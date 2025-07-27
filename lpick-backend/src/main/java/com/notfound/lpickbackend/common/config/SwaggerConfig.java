package com.notfound.lpickbackend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://lpick.duckdns.org").description("Production"),
                        new Server().url("http://localhost:8080").description("Local Dev")
                ))
                .addSecurityItem(new SecurityRequirement().addList("kakao"))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("kakao", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("https://kauth.kakao.com/oauth/authorize")
                                                .tokenUrl("https://kauth.kakao.com/oauth/token")
                                                .scopes(new Scopes()
                                                        .addString("profile_nickname", "Access to nickname")
                                                        .addString("profile_image", "Access to profile image")
                                                )
                                        )
                                )
                        )
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("access_token")
                        )
                );
    }
}
