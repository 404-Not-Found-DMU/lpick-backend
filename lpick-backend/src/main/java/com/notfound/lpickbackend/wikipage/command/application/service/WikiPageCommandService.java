package com.notfound.lpickbackend.wikipage.command.application.service;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.wikipage.command.application.domain.WikiStatus;
import com.notfound.lpickbackend.wikipage.command.application.dto.request.WikiStatusRequest;
import com.notfound.lpickbackend.wikipage.command.repository.WikiPageCommandRepository;
import com.notfound.lpickbackend.wikipage.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WikiPageCommandService {
    private final WikiPageQueryService wikiPageQueryService;

    private final WikiPageCommandRepository wikiPageCommandRepository;

    @Transactional
    public String createWikiPage(String title) {

        if (title == null) {
            throw new CustomException(ErrorCode.EMPTY_TITLE); // 잘못된 필드 데이터 예외
        }

        try {
            WikiPage newWikiPage = WikiPage.builder()
                    .title(title)
                    .currentRevision("r1") // 초기 생성시 r1
                    .wikiStatus(WikiStatus.OPEN)
                    .build();

            wikiPageCommandRepository.save(newWikiPage);

            return newWikiPage.getWikiId();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateWikiPageCurrentRevision(String wikiId, String newRevisionNumber) {
        try {
            WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);
            wikiPage.updateCurrentRevision(newRevisionNumber);

            wikiPageCommandRepository.save(wikiPage);
        } catch (Exception e) {
            throw new CustomException((ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Transactional
    public void updateWikiPageStatus(String wikiId, WikiStatusRequest req) {
        try {
            WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);
            wikiPage.updateWikiStatus(req.getStatus());

            wikiPageCommandRepository.save(wikiPage);
        } catch (Exception e) {
            throw new CustomException((ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    public void updateWikiPage(WikiPage wikiPage) {
        wikiPageCommandRepository.save(wikiPage);
    }
}
