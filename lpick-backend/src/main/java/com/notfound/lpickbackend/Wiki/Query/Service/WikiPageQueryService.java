package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import com.notfound.lpickbackend.Wiki.Query.Repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WikiPageQueryService {
    private final WikiPageQueryRepository wikiPageQueryRepository;

    @Transactional
    public WikiPage getWikiPageById(String wikiId) {
        return wikiPageQueryRepository.findById(wikiId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "WikiPageQueryService, getWikiPageById : wikiId에 해당하는 WikiPage가 존재하지 않습니다."));
    }
}
