package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.DebateRequest;
import com.notfound.lpickbackend.wiki.command.repository.DebateCommandRepository;
import com.notfound.lpickbackend.wiki.command.repository.WikiPageCommandRepository;
import com.notfound.lpickbackend.wiki.query.repository.PageRevisionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateCommandService {

    private final WikiPageCommandRepository wikiPageCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;
    private final DebateCommandRepository debateCommandRepository;
    private final PageRevisionQueryRepository pageRevisionQueryRepository;

    public String createDebate(DebateRequest req, String wikiId, OAuth2UserDetails userDetail) {

        UserInfo user = userInfoCommandRepository.findByOauthId(userDetail.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));

        WikiPage wiki = wikiPageCommandRepository.findById(wikiId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));

        PageRevision revision = pageRevisionQueryRepository.findById(req.getRevisionId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));

        Debate debate = Debate.builder()
                .debateName(req.getDebateName())
                .debateSubject(req.getDebateSubject())
                .isEnd(DebateStatus.OPEN)
                .wiki(wiki)
                .pageRevision(revision)
                .oauth(user)
                .build();

        Debate resultDebate = debateCommandRepository.save(debate);

        return resultDebate.getDtId();
    }

    /** status 수정. OPEN으로의 수정은 불가능(토론 재시작 없음) */
    public void patchDebateStatus(String debateId, DebateStatus status/*, OAuth2UserDetails userDetail*/) {
        Debate debate = debateCommandRepository.findById(debateId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));

        debate.patchStatus(status);
        debateCommandRepository.save(debate);
    }
}
