package com.notfound.lpickbackend.common.websocket;

import com.notfound.lpickbackend.common.websocket.interceptor.StompAuthChannelInterceptor;
import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwt;


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompAuthChannelInterceptor(jwt));
    }
}