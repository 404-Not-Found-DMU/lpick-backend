package com.notfound.lpickbackend.common.websocket.stomp_rule;

import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


/** WebSocket 연결이 성립되기 전 수행하는 http 요청에서 쿠키를 꺼내 검증 및 principal 사용을 위한 기반 값(attrs)으로 저장해두는 로직. */
@RequiredArgsConstructor
@Slf4j
public class BeforeWebSocketHandShakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwt;

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler h, Map<String,Object> attrs) {
        log.info("핸드셰이크 검증들어갑니다잉");
        if (req instanceof ServletServerHttpRequest sr && sr.getServletRequest().getCookies()!=null) {
            for (var c : sr.getServletRequest().getCookies()) {
                if ("access_token".equals(c.getName())) {
                    String token = c.getValue();
                    if (jwt.validateToken(token)) {
                        log.info("사쿠라여?");
                        attrs.put("uid", jwt.getSubject(token)); // ★ 최소 정보만 저장
                    }
                    break;
                }
            }
        }
        return true;
    }
    @Override public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s, WebSocketHandler w, Exception e) {}
}