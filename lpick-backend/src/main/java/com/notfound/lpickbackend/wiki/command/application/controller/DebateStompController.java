package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common.exception.stomp.StompAppException;
import com.notfound.lpickbackend.common.exception.stomp.StompErrorCode;
import com.notfound.lpickbackend.common.websocket.dto.DebateChatMessage;
import com.notfound.lpickbackend.wiki.command.application.service.DebateChatCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DebateStompController {

    private final SimpMessagingTemplate template;
    private final DebateChatCommandService debateChatCommandService;


    @MessageMapping("/rooms/{roomId}/send")
    public void push(@DestinationVariable String roomId, @RequestBody Map<String, Object> body,  Principal principal) {


        log.info("send 메시지 확인..");

        // 1) 로그인 여부 체크 (익명 또는 null이면 soft-deny)
        boolean anonymous = (principal == null)
                || (principal.getName() == null)
                || principal.getName().startsWith("anonymous");

        if (anonymous) {
            log.info("비로그인이요~");
            throw new StompAppException(StompErrorCode.MESSAGE_SEND_MUST_NEED_LOGIN);
        }

        log.info("로그인이요~");

        // 2) 정상 처리 (필요 시 senderId 등 세팅)
        // body.put("senderId", principal.getName()); // 필요하면 사용
        template.convertAndSend("/topic/rooms/" + roomId, body);
    }

    @MessageMapping("/debate/{debateId}/send")
    public void broadcastDebateChat(
            @DestinationVariable String debateId,
            @RequestBody DebateChatMessage body, Principal principal) {


        log.info("send 메시지 확인..");

        // 1) 로그인 여부 체크 (익명 또는 null이면 soft-deny)
        boolean anonymous = (principal == null)
                || (principal.getName() == null)
                || principal.getName().startsWith("anonymous");

        if (anonymous) {
            log.info("비로그인이요~");
            throw new StompAppException(StompErrorCode.MESSAGE_SEND_MUST_NEED_LOGIN);
        }

        log.info("로그인이요~");

        debateChatCommandService.saveChat(debateId, body, principal.getName());

        // 2) 정상 처리 (필요 시 senderId 등 세팅)
        // body.put("senderId", principal.getName()); // 필요하면 사용
        template.convertAndSend("/topic/debate/" + debateId, body);
    }
}
