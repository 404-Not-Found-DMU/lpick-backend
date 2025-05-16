package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import com.notfound.lpickbackend.Wiki.Query.Repository.WikiPageQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WikiPageQueryService {
    private final WikiPageQueryRepository wikiPageQueryRepository;

    public WikiPage getWikiPageById(String wikiId) {

        return wikiPageQueryRepository.findById(wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));
    }
}
