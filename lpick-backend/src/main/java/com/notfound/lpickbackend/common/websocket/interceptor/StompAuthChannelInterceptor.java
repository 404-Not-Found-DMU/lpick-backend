package com.notfound.lpickbackend.common.websocket.interceptor;

import com.notfound.lpickbackend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String USER_PREFIX   = "/user/";
    private static final String USER_ERR_DEST = "/user/queue/errors";
    private static final String PUBLIC_ROOM   = "/topic/rooms/";

    private final JwtUtil jwt;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var acc = StompHeaderAccessor.wrap(message);
        var cmd = acc.getCommand();
        if (cmd == null) return message; // 하트비트/내부메시지 등

        // 공통 토큰 추출
        String token = extractToken(acc);
        boolean authenticated = (token != null && jwt.validateToken(token));


        /* SEND == 클라 to 서버로 메시지 보내는 경우의 stompCOMMAND. 현재 서버는 토론방에 대한 의견 제공(채팅)시에만 send 활용하므로 해당 내역에 대해 처리  */
        if (cmd == StompCommand.SEND) {
            // 컨트롤러에서 soft-deny 처리할 것이므로, 여기선 Principal만 보장
            if (authenticated && acc.getUser() == null) {
                acc.setUser(toAuth(jwt.getSubject(token)));
            }
            if (!authenticated && acc.getUser() == null) {
                acc.setUser(toAnonymous(acc));
            }
        }
        else if (StompCommand.SUBSCRIBE.equals(cmd)) {
            log.info("구독검증");
            final String dest = acc.getDestination();
            final boolean authenticatedInAccessor = isAuthenticatedInAccessor(acc);

            // 1) 개인 큐 정책
            if (startsWith(dest, USER_PREFIX)) {
                
                if (!authenticatedInAccessor) {
                    // 익명은 에러 큐만 허용
                    if (USER_ERR_DEST.equals(dest)) {
                        log.info("익명에러큐섭스크라이브");
                        return message; // 허용
                    }

                    // (A) 연결 유지형 soft-deny: 이 SUBSCRIBE만 드롭
                    // 필요 시: 여기서 로깅
                    // log.debug("Anonymous SUBSCRIBE blocked: dest={}, sid={}", dest, acc.getSessionId());

                    // (선택) 안내를 보내고 싶다면 '이벤트 발행→리스너에서 전송'이나,
                    //       컨트롤러 경로에서의 소프트 디나이로 일관 처리 권장.
                    //       아래는 예시 (비권장: 인터셉터가 템플릿 의존하면 순환 리스크)
                    //
                    // publisher.publishEvent(new SoftDenyEvent(this, acc.getSessionId(),
                    //        "SUBSCRIBE_NEEDS_LOGIN", "로그인이 필요합니다."));

                    return null; // 프레임만 드롭 → 구독 미등록, 연결 유지
                }

                log.info("로그인에러큐섭스크라이브");

                // 로그인 사용자의 /user/** 구독은 허용 (실제 라우팅은 자신의 세션/Principal로만 맵핑됨)
                return message;
            }

            // 2) 공개 토픽은 허용
            if (startsWith(dest, PUBLIC_ROOM)) {
                return message;
            }

            // 3) 알 수 없는 목적지: 드롭(연결 유지). 필요하면 로깅/알림
            // log.debug("Invalid SUBSCRIBE dest blocked: dest={}, user={}", dest, acc.getUser());
            return null;
        }
        else {
            if (authenticated) acc.setUser(toAuth(jwt.getSubject(token)));
            else acc.setUser(toAnonymous(acc));
        }

        // SUBSCRIBE는 정책에 따라 필요 시 추가(익명 에러큐 허용 등)
        return message;
    }


    // 간단한 Principal 구현
//    public static class StompUserPrincipal implements Principal {
//        private final String name;
//        public StompUserPrincipal(String name) { this.name = name; }
//        @Override public String getName() { return name; }
//    }

    // 헤더에서 jwt 추출
    private String extractToken(StompHeaderAccessor acc) {
        String auth = acc.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        Map<String, Object> attrs = acc.getSessionAttributes();
        if (attrs != null) {
            Object t = attrs.get("token");
            if (t instanceof String s && StringUtils.hasText(s)) return s;
        }
        return null;
    }

    private Principal toAuth(String userId) {
        return new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Principal toAnonymous(StompHeaderAccessor acc) {
        String sessionId = Optional.ofNullable(acc.getSessionId()).orElse(UUID.randomUUID().toString());
        return new AnonymousAuthenticationToken(
                "ws-anonymous-key", "anonymous:" + sessionId,
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    }

    private boolean isAuthenticatedInAccessor(StompHeaderAccessor acc) {
        return acc.getUser() != null
                && StringUtils.hasText(acc.getUser().getName())
                && !acc.getUser().getName().startsWith("anonymous");
    }

    private boolean startsWith(String s, String prefix) {
        return s != null && s.startsWith(prefix);
    }
}
