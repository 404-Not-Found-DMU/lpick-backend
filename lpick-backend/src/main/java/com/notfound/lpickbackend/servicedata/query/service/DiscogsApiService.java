package com.notfound.lpickbackend.servicedata.query.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DiscogsApiService {

    private final WebClient discogsWebClient; // (2)에서 설정한 Bean 주입

    public String getPrimaryImageUrl(String masterId) {
        try {
            JsonNode response = discogsWebClient.get()
                    .uri("/releases/{id}", masterId)
                    .retrieve() // 요청 실행
                    .bodyToMono(JsonNode.class) // 응답을 JsonNode로 받음
                    .block(); // (동기식으로 대기. 비동기로 처리할 수도 있습니다.)

            if (response != null && response.has("images")) {
                for (JsonNode imageNode : response.get("images")) {
                    if (imageNode.has("type") && "primary".equals(imageNode.get("type").asText())) {
                        return imageNode.get("resource_url").asText();
                    }
                }
            }
        } catch (Exception e) {
            // log.error("Discogs API 호출 실패: masterId={}", masterId, e);
            return null;
        }
        return null; // 'primary' 이미지가 없는 경우
    }
}
