package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.debate.query.repository.DebateQueryRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.query.dto.response.DebateHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebateQueryService {
    private final DebateQueryRepository debateQueryRepository;

    public List<DebateHeader> findDebateListByWikiId(String wikiId) {
        return debateQueryRepository.findAllDebateHeaderByWikiId(wikiId);
    }

    public boolean isDebateOpen(String debateId) {
        Debate targetDebate = debateQueryRepository.findById(debateId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));
        return targetDebate.getIsEnd() == DebateStatus.OPEN;
    }
}
