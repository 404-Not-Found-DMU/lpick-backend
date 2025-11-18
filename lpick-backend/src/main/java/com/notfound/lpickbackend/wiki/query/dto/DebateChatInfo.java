package com.notfound.lpickbackend.wiki.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebateChatInfo {
    private String chatId;
    private String userId; // 클라이언트가 소유한 jwt의 subject와 비교하기위한 값.
    private String userNickname; // 채팅 사용자 이름
    private String content;
    private Instant createdAt;
    private Boolean isBlind; // 가림처리되어있는지(true인 경우 프론트엔드에서 흐림처리 + 클릭하여 확인)
    private String isAnswerTo; // 누군가에 대한 답변 글인경우 해당 글에 대한 chatId를 명시
    private String profile;
}
