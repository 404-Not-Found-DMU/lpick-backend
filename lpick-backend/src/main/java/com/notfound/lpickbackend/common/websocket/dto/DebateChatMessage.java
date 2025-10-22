package com.notfound.lpickbackend.common.websocket.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

/** 토론 채팅을 구현하기위한 dto */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DebateChatMessage {
    @NotBlank private String roomId;
    @NotBlank private String content;

    private String senderId;          // 서버에서 Principal로 세팅
    private Instant sentAt;           // 서버에서 세팅
    private MessageType type;         // ENTER, MESSAGE, LEAVE

    public enum MessageType { ENTER, MESSAGE, LEAVE }
}
