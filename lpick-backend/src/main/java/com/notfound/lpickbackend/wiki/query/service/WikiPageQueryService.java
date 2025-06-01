package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiPageQueryService {
    private final WikiPageQueryRepository wikiPageQueryRepository;

    public WikiPage getWikiPageById(String wikiId) {
        return wikiPageQueryRepository.findById(wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));
    }
}
