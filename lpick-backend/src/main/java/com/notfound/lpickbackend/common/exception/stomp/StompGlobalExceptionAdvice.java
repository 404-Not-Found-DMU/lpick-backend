package com.notfound.lpickbackend.common.exception.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class StompGlobalExceptionAdvice {

    private final ObjectProvider<SimpMessagingTemplate> templateProvider;

    // stomp 관련 예외
    @MessageExceptionHandler(StompAppException.class)
    public void handleAppExceptions(StompAppException e,
                                    Principal principal,
                                    @Header("simpSessionId") String sessionId) {
        // 1) 끊어야 하는 에러면: 아무 것도 보내지 말고 예외 재던지기
        if (e.getErrorCode().disconnectOnError) {
            // 필요시 로그만 남김
            // log.warn("Disconnecting due to error: code={}, sid={}", e.getErrorCode().name(), sessionId);
            throw new org.springframework.messaging.MessagingException(
                    e.getErrorCode().name() + ": " + (e.getMessage() != null ? e.getMessage() : "disconnect"));
        }

        // 2) soft-deny 모드: 개인 에러 큐로 안내 (연결 유지)
        var template = templateProvider.getIfAvailable();
        if (template == null) return;

        var payload = new LinkedHashMap<>();
        // 필수값(가능하면 null 아닌 값으로 보장)
        payload.put("name",   e.getErrorCode().name());
        payload.put("status", e.getErrorCode().status);

        // 선택값(null 안전)
        if (e.getErrorCode().errorMessage != null) payload.put("dev_message", e.getErrorCode().errorMessage);
        if (e.getMessage() != null)                payload.put("message",     e.getMessage());
        if (e.getDetails() != null)                payload.put("details",     e.getDetails());

        // principal 또는 name이 없거나, 설정된 name이 anonymous로 시작하면 비로그인 상태로 간주하고 세션 아이디를 기반으로 반환한다.
        boolean loggedIn = principal != null
                && principal.getName() != null
                && !principal.getName().startsWith("anonymous");

        if (loggedIn) {
            template.convertAndSendToUser(principal.getName(), "/queue/errors", payload);
        } else {
            var sha = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            sha.setSessionId(sessionId);
            sha.setLeaveMutable(true);
            template.convertAndSendToUser(sessionId, "/queue/errors", payload, sha.getMessageHeaders());
        }
    }
}