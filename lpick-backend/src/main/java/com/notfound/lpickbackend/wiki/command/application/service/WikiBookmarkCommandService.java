package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.repository.WikiBookmarkCommandRepository;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Transactional
    public void deleteWikiBookmarkById(String wikiId, String oauthId) {

        WikiBookmark targetBookmark = wikiBookmarkCommandRepository
                .findByWiki_WikiIdAndOauth_OauthId(wikiId, oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOKMARK));

        wikiBookmarkCommandRepository.delete(targetBookmark);
    }

    public void deleteAllBookmarkDataByWiki_WikiId(String wikiId) {
        wikiBookmarkCommandRepository.deleteAllByWiki_WikiId(wikiId);
    }
}
