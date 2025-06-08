package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserIdNamePairResponse;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.command.repository.PageRevisionCommandRepository;
import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageRevisionCommandService {

    private final WikiPageCommandService wikiPageCommandService;
    private final WikiPageQueryService wikiPageQueryService;

    private final PageRevisionQueryService pageRevisionQueryService;


    private final PageRevisionCommandRepository pageRevisionCommandRepository;

    @Transactional
    public PageRevisionResponse createNewRevision(PageRevisionRequest request, String wikiId, UserInfo user) {

        // WikiId 값을 지니는 wikiPage 엔티티가 존재하는지 확인.
        WikiPage targetWikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        // 기존 버전 + 1 하기 위한 리비전 개수 카운팅
        // count vs 제일 높은값 1개 뽑기 중 하나 sql문 효율성 비교 필요
        // 문제점 : 리비전을 등록하는 순간 카운팅해오면, 여러 인원이 동시 수정시 문제가 발생할 수 있음.
        // 1. DB에 대한 동시성 관리 수행(낙관적/비관적락)하여 한 인원의 작성 요청 트랜잭션 종료시까지 DB 단위 잠그기
        // 2. 다른 방법 찾기...?
        long revisionNumber = pageRevisionQueryService.wikicountByWiki_WikiId(wikiId);

        // entity 저장하여 id, createdAt 기입된채로 가져오기
        PageRevision newPageRevision = PageRevision.builder()
                .revisionNumber("r" + (revisionNumber + 1))
                .content(request.getContent())
                .wiki(targetWikiPage)
                .userInfo(user)
                .build();

        PageRevision saveResult = pageRevisionCommandRepository.save(newPageRevision);

        // 해당 위키문서에 대해 새롭게 작성된 currentRevision을 지니도록 업데이트.
        wikiPageCommandService.updateWikiPageCurrentRevision(targetWikiPage.getWikiId(), saveResult.getRevisionNumber());

        // entity 기반 변경.
        // 원래라면 mapper 사용해야하나, 외부 라이브러리 쓸지 결정해야하므로 일단 빌더로 기입.
        return PageRevisionResponse.builder()
                .revisionId(saveResult.getRevisionId())
                .content(saveResult.getContent())
                .createdAt(saveResult.getCreatedAt())
                .createWho(UserIdNamePairResponse.builder()
                        .oauthId(saveResult.getUserInfo().getOauthId())
                        .nickName(saveResult.getUserInfo().getNickname())
                        .build())
                .build();
    }

    public void deleteRevisionDataByWiki_WikiId(String wikiId) {
        pageRevisionCommandRepository.deleteByWiki_WikiId(wikiId);
    }
}
