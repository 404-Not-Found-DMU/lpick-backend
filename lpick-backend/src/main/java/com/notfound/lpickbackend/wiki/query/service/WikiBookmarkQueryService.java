package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiBookmarkResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageBookmarkListResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiBookmarkQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WikiBookmarkQueryService {

    private final WikiBookmarkQueryRepository wikiBookmarkQueryRepository;



    public WikiBookmarkResponse findByWikiIdAndOauthId(String wikiId, String oauthId) {

        Optional<WikiBookmark> wikiBookmarkOptional = findByWiki_WikiIdAndOauth_oauthId(wikiId, oauthId);
        String bookmarkId = wikiBookmarkOptional.map(WikiBookmark::getWikiBookmarkId).orElse(null);

        return WikiBookmarkResponse.builder()
                .wikiBookmarkId(bookmarkId)
                .build();
    }

    public Page<WikiPageBookmarkListResponse> getWikiBookmarkListByUserId(Pageable pageable, UserInfo userInfo) {

        Page<WikiBookmark> wikiBookmarkList = wikiBookmarkQueryRepository.findByOauth_OauthId(userInfo.getOauthId(), pageable);

        return wikiBookmarkList.map(bookmark -> {
            // findByOauth_OauthId()로 불러와진 WikiBookmark Entity는 EntityGraph("wiki")를 사용했으므로 N+1 문제 걱정 없음
            WikiPage wikipage = bookmark.getWiki();
            return WikiPageBookmarkListResponse.builder()
                    .wikiBookmarkId(bookmark.getWikiBookmarkId())
                    .wikiPageId(wikipage.getWikiId())
                    .wikiTitle(wikipage.getTitle())
                    .build();
        });
    }

    public boolean existsByWiki_WikiIdAndOauth_oauthId(String wikiId, String oauthId) {
        return wikiBookmarkQueryRepository.existsByWiki_WikiIdAndOauth_oauthId(wikiId, oauthId);
    }

    public Optional<WikiBookmark> findByWiki_WikiIdAndOauth_oauthId(String wikiId, String oauthId) {
        return wikiBookmarkQueryRepository.findByWiki_wikiIdAndOauth_oauthId(wikiId, oauthId);

    }

    public boolean existsById(String bookmarkId) {
        return wikiBookmarkQueryRepository.existsById(bookmarkId);
    }

    public Optional<WikiBookmark> findById(String bookmarkId) {
        return wikiBookmarkQueryRepository.findById(bookmarkId);
    }
}
