package com.notfound.lpickbackend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${discogs.token}")
    private String discogsToken;

    @Bean
    public WebClient discogsWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.discogs.com")
                .defaultHeader("Authorization", "Discogs token="+ discogsToken)
                .defaultHeader("User-Agent", "LPickApp/1.0")
                .build();
    }
}
