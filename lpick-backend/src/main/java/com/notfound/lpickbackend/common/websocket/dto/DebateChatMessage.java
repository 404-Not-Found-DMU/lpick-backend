package com.notfound.lpickbackend.common.websocket.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

/** 토론 채팅을 구현하기위한 dto */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DebateChatMessage {
    private String parentDebateChatId;
    @NotBlank private String content;
}
