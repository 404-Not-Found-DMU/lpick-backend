package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
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
