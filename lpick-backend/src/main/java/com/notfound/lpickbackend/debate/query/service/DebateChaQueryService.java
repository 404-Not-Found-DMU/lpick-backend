package com.notfound.lpickbackend.debate.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.DebateChat;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.debate.query.repository.DebateChatQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateChaQueryService {

    private final DebateChatQueryRepository debateChatQueryRepository;

    public DebateChat findById(String id) {
        return debateChatQueryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE_CHAT));
    }

    public int countByOauthId(String oauthId) {
        return debateChatQueryRepository.countByOauth_OauthId(oauthId);
    }
}
