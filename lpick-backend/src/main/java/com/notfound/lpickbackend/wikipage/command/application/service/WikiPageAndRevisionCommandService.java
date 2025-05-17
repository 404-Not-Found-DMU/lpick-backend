package com.notfound.lpickbackend.wikipage.command.application.service;


import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.command.application.service.PageRevisionCommandService;
import com.notfound.lpickbackend.wikipage.command.application.dto.WikiPageCreateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * WikiPage와 Revision 생성 서비스 로직을 하나의 트랜잭션에서 수행하기 위한 서비스
 * */
@Service
@RequiredArgsConstructor
public class WikiPageAndRevisionCommandService {

    private final PageRevisionCommandService pageRevisionCommandService;

    private final WikiPageCommandService wikiPageCommandService;

    private final UserInfoQueryService userInfoQueryService;

    @Transactional
    public void createWikiPageAndRevision(WikiPageCreateRequestDTO wikiRequestDTO) {

        String wikiId = wikiPageCommandService.createWikiPage(wikiRequestDTO.getTitle());

        PageRevisionRequest pageRevisionRequestDTO = PageRevisionRequest.
                builder().
                wikiId(wikiId).
                content(wikiRequestDTO.getContent()).
                build();

        UserInfo userInfo = getUserInfo(wikiRequestDTO.getUserId());

        pageRevisionCommandService.createNewRevision(pageRevisionRequestDTO, userInfo);
    }

    // 유저 정보를 가져오기 위한 메소드
    private UserInfo getUserInfo(String userId) {

        return userInfoQueryService.getUserInfoById(userId);
    }
}
