package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.repository.WikiBookmarkCommandRepository;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WikiBookmarkCommandService {
    private final WikiPageQueryService wikiPageQueryService;

    private final WikiBookmarkCommandRepository wikiBookmarkCommandRepository;

    @Transactional
    public WikiBookmark createNewWikiBookmark(String wikiId, UserInfo userInfo) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        return wikiBookmarkCommandRepository.save(WikiBookmark.builder()
                .oauth(userInfo).wiki(wikiPage).build()
        );

    }

    public void deleteWikiBookmarkByWiki_wikiIdAndOauth_oauthId(String wikiId, UserInfo userInfo) {
        wikiBookmarkCommandRepository.deleteByWiki_wikiIdAndOauth_oauthId(wikiId, userInfo.getOauthId());
    }

    public void deleteWikiBookmarkById(String bookmarkId) {
        wikiBookmarkCommandRepository.deleteById(bookmarkId);
    }

    public void deleteAllBookmarkDataByWiki_WikiId(String wikiId) {
        wikiBookmarkCommandRepository.deleteAllByWiki_WikiId(wikiId);
    }
}
