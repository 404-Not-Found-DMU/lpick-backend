package com.notfound.lpickbackend.debate.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.debate.query.repository.DebateChatQueryRepository;
import com.notfound.lpickbackend.debate.query.repository.DebateQueryRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateChat;
import com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebateChatQueryService {
    private final DebateQueryRepository debateQueryRepository;
    private final DebateChatQueryRepository debateChatQueryRepository;

    public List<DebateChatInfo> readDebateChatList(String debateId) {
        return debateChatQueryRepository.findAllByDtIdOrderByCreatedAtDesc(debateId);
    }

    public DebateChat findById(String id) {
        return debateChatQueryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE_CHAT));
    }

    public int countByOauthId(String oauthId) {
        return debateChatQueryRepository.countByOauth_OauthId(oauthId);
    }
}
