package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateSubject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DebateHeader {
    private String debateId;
    private String debateName;
    private DebateSubject debateSubject;
    private String debateWriter;
    private Instant createdAt; // 생성 시각
    private Instant updateAt; // 최신 의견(debateChat)이 올라온 시각
    private Long chatCount; // 의견 개수
    private DebateStatus status;
}
