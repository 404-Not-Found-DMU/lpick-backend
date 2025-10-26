package com.notfound.lpickbackend.common.websocket.interceptor;

import com.notfound.lpickbackend.security.util.JwtUtil;
import com.notfound.lpickbackend.wiki.query.service.DebateQueryService;
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
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * StompCommand별로 보안 및 검증 처리를 위한 로직을 모아둔 인터셉터.
 * SEND == 사용자의 로그인 상태에 따라 principal을 채워두기 위한 로직.
 * SUBSCRIBE ==
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {


    private static final AntPathMatcher MATCHER = new AntPathMatcher(); // Destination의 PathVariable 뽑기 위한 클래스
    private static final String USER_PREFIX   = "/user/";
    private static final String USER_ERR_DEST = "/user/queue/errors";
    private static final String TOPIC_PREFIX   = "/topic/"; // subscribe의 대상이 되는 전치사
    private static final String DEBATE_TOPIC_PATTERN = "/topic/debate/{debateId}"; // 토론방 구독 목적의 경로 양식

    private final JwtUtil jwt;

    private final DebateQueryService debateQueryService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var acc = StompHeaderAccessor.wrap(message);
        var cmd = acc.getCommand();
        if (cmd == null) return message; // 하트비트/내부메시지 등

        // 공통 토큰 추출
        String token = extractToken(acc);
        boolean authenticated = (token != null && jwt.validateToken(token));


        /* SEND == 클라 to 서버로 메시지 보내는 경우의 stompCOMMAND. 현재 서버는 토론방에 대한 의견 제공(채팅)시에만 send 활용하므로 해당 내역에 대해 처리  */
        // SEND, SUBSCRIBE 등 모든 클라이언트에게 전송받은 메시지에서 jwt를 검증한다.
        if (authenticated && acc.getUser() == null) {
            acc.setUser(toAuth(jwt.getSubject(token)));
        }
        if (!authenticated && acc.getUser() == null) {
            acc.setUser(toAnonymous(acc));
        }


        if (StompCommand.SUBSCRIBE.equals(cmd)) {
            log.info("구독검증");
            final String dest = acc.getDestination();
            final boolean authenticatedInAccessor = isAuthenticatedInAccessor(acc);

            // 1) 공개 토픽은 허용
            if (startsWith(dest, TOPIC_PREFIX)) {

                // 토론방 구독인 경우 토론방의 상태를 검증하고 구독여부 결정
                // destination이 null이 아니고, 경로 패턴이 '토론방 구독'목적의 경로와 매칭되는 경우
                if (dest != null && MATCHER.match(DEBATE_TOPIC_PATTERN, dest)) {
                    log.info("토론방섭스크라이브");
                    
                    String debateId = MATCHER
                            .extractUriTemplateVariables(DEBATE_TOPIC_PATTERN, dest)
                            .get("debateId");

                    // 여기서 서비스 호출
                    if (!debateQueryService.isDebateOpen(debateId)) {
                        // soft-deny 처리 (프레임 드롭 or 세션 타게팅 안내 전송)
                        return null;
                    }
                }

                log.info("공개여~");
                return message;
            }

            // 2) 개인 큐 정책
            if (startsWith(dest, USER_PREFIX)) {
                
                if (!authenticatedInAccessor) {
                    // 익명은 에러 큐만 허용 <- 익명이 서비스 내 알림 큐(새 댓글 등) 등을 구독하는 고려하지 않은 상황 발생 막기위함! 근데 알림이 없네..
                    if (USER_ERR_DEST.equals(dest)) {
                        log.info("익명에러큐섭스크라이브");
                        return message; // 허용
                    }

                    return null; // 프레임만 드롭 → 구독 미등록, 연결 유지
                }

                log.info("로그인에러큐섭스크라이브");

                // 로그인 사용자의 /user/** 구독은 허용 (실제 라우팅은 자신의 세션/Principal로만 맵핑됨)
                return message;
            }

            // 3) 알 수 없는 목적지: 드롭(연결 유지). 필요하면 로깅/알림
            // log.debug("Invalid SUBSCRIBE dest blocked: dest={}, user={}", dest, acc.getUser());
            return null;
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
        // authorization 헤더 기반 사용버전. 쿠키를 js단에서 까뒤집어야 한다는 점에서 사용 불가! 쿠키에 https only 쓸거니까..
//        String auth = acc.getFirstNativeHeader("Authorization");
//        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
//            return auth.substring(7);
//        }
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
