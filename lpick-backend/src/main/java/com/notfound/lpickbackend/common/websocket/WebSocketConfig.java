package com.notfound.lpickbackend.common.websocket;

import com.notfound.lpickbackend.common.websocket.stomp_rule.BeforeWebSocketHandShakeInterceptor;
import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwt; // 생성자 주입

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-test")                           // ← 순수 WebSocket(네이티브)용. postman을 통한 테스트 목적으로, 배포시에는 비활성화할 예정!
                .addInterceptors(cookieHandshakeInterceptor())
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws")
                .addInterceptors(cookieHandshakeInterceptor())
                .setAllowedOriginPatterns("*")    // ::: 실제 배포 후 도메인 추가 예정 :::
                .withSockJS();
    }

    @Bean
    public BeforeWebSocketHandShakeInterceptor cookieHandshakeInterceptor() {
        return new BeforeWebSocketHandShakeInterceptor(jwt);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");  // 서버→클라 브로커. 전자는 서버 to 다수, 후자는 서버 to 1인
        registry.setApplicationDestinationPrefixes("/app"); // 클라→서버 진입 프리픽스
        registry.setUserDestinationPrefix("/user");         // /user/queue/** 와 같이 개인에게 보내는 경우 사용하는 전용의 prefix. 개별 '에러' 알림 기능 위해서 사용(개별 댓글 알림 등에도 사용)
    }
}
