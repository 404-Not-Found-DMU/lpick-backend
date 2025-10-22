package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common.exception.stomp.StompAppException;
import com.notfound.lpickbackend.common.exception.stomp.StompErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DebateStompController {

    private final SimpMessagingTemplate template;



    @MessageMapping("/rooms/{roomId}/send")
    public void push(@DestinationVariable String roomId, @RequestBody Map<String, Object> body,  Principal principal,
                     @Header("simpSessionId") String sessionId) {


        log.info("send 메시지 확인..");

        // 1) 로그인 여부 체크 (익명 또는 null이면 soft-deny)
        boolean anonymous = (principal == null)
                || (principal.getName() == null)
                || principal.getName().startsWith("anonymous");

        if (anonymous) {
            throw new StompAppException(StompErrorCode.MESSAGE_SEND_MUST_NEED_LOGIN);
//            var errorPayload = Map.of(
//                    "code", "MESSAGE_SEND_MUST_NEED_LOGIN",
//                    "message", "로그인이 필요합니다."
//            );
//            log.info("비로그인이요~");
//
//            // 세션 타게팅 헤더 (익명은 userName이 없으므로 sessionId로 지정)
//            var sha = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
//            sha.setSessionId(sessionId);   // ★ 이 세션으로만
//            sha.setLeaveMutable(true);
//
//            template.convertAndSendToUser(sessionId, "/queue/errors",
//                    errorPayload, sha.getMessageHeaders());
//            return; // soft-deny: 방송하지 않음 (연결은 유지)
        }

        log.info("로그인이요~");

        // 2) 정상 처리 (필요 시 senderId 등 세팅)
        // body.put("senderId", principal.getName()); // 필요하면 사용
        template.convertAndSend("/topic/rooms/" + roomId, body);
    }
}
