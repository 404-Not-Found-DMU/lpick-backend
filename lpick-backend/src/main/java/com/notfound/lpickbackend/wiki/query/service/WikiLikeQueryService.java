package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiLike;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiLikeResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiLikeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WikiLikeQueryService {
    private final WikiLikeQueryRepository wikiLikeQueryRepository;

    @Transactional(readOnly = true)
    public WikiLikeResponse getWikiLikeByWikiId(String wikiId, String oauthId) {
        Optional<WikiLike> targetWikiLike = wikiLikeQueryRepository.findByWiki_WikiIdAndOauth_OauthId(wikiId, oauthId);

        if(targetWikiLike.isEmpty()) return new WikiLikeResponse(false, null);
        else return new WikiLikeResponse(true, targetWikiLike.get().getWikiLikeId());
    }

}
