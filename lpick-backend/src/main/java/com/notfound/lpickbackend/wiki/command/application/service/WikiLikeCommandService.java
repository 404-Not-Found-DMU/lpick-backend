package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiLike;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.repository.WikiLikeCommandRepository;
import com.notfound.lpickbackend.wiki.command.repository.WikiPageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WikiLikeCommandService {
    private final WikiLikeCommandRepository wikiLikeCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;
    private final WikiPageCommandRepository wikiPageCommandRepository;

    @Transactional
    public void createWikiLike(String wikiId, String oauthId) {

        if(wikiLikeCommandRepository.existsByWiki_WikiIdAndOauth_OauthId(wikiId, oauthId))
            throw new CustomException(ErrorCode.ALREADY_HAS_LIKE);

        UserInfo user = userInfoCommandRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));
        WikiPage wikiPage = wikiPageCommandRepository.findById(wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));

        WikiLike wikiLike = WikiLike.builder()
                .oauth(user)
                .wiki(wikiPage)
                .build();

        wikiLikeCommandRepository.save(wikiLike);
    }

    @Transactional
    public void deleteWikiLike(String wikiId, String oauthId) {
        WikiLike targetLike = wikiLikeCommandRepository
                .findByWiki_WikiIdAndOauth_OauthId(wikiId, oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI_LIKE));

        wikiLikeCommandRepository.delete(targetLike);
    }
}
