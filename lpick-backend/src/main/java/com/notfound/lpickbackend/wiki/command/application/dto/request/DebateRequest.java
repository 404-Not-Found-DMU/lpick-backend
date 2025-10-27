package com.notfound.lpickbackend.wiki.command.application.dto.request;

import com.notfound.lpickbackend.wiki.command.application.domain.DebateSubject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 토론 생성 시 사용. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DebateRequest {
    private String debateName;
    private DebateSubject debateSubject;
    private String revisionId;
}
