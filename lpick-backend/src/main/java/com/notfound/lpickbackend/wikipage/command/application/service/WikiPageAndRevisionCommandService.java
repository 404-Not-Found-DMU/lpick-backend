package com.notfound.lpickbackend.wikipage.command.application.service;


import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userInfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.command.application.service.PageRevisionCommandService;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wikipage.command.application.dto.request.WikiPageCreateRequestDTO;
import com.notfound.lpickbackend.wikipage.query.service.WikiPageQueryService;
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
    private final PageRevisionQueryService pageRevisionQueryService;

    private final WikiPageCommandService wikiPageCommandService;
    private final WikiPageQueryService wikiPageQueryService;

    private final UserInfoQueryService userInfoQueryService;

    @Transactional
    public void createWikiPageAndRevision(WikiPageCreateRequestDTO wikiRequestDTO) {

        String wikiId = wikiPageCommandService.createWikiPage(wikiRequestDTO.getTitle());

        PageRevisionRequest pageRevisionRequestDTO = PageRevisionRequest.
                builder().
                content(wikiRequestDTO.getContent()).
                build();

        UserInfo userInfo = getUserInfo(wikiRequestDTO.getUserId());

        pageRevisionCommandService.createNewRevision(pageRevisionRequestDTO, wikiId, userInfo);
    }

    @Transactional
    public void revertWikiPageAndRevision(String wikiId, String targetRevisionId) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);
        PageRevision revertRevision = pageRevisionQueryService.getPageRevisionById(targetRevisionId);

        wikiPage.updateCurrentRevision(revertRevision.getRevisionId());

        wikiPageCommandService.updateWikiPage(wikiPage);
    }
    @Transactional
    public void hardDeleteWikiPage(String wikiId) {

        /** wikiPage와 연관관계를 지니는 PageRevision을 우선적으로 모두 삭제한다. */
        // JPA CASCADE 기반하에 삭제할 수 있지만 다음과 같이 리포지토리에서 연관 데이터 한번에 찾아 삭제하는 방식을 사용한다.
        // CASCADE 방식은 매우 많은 양의 자식엔티티를 삭제해야하는경우 엔티티 하나하나에 전부 SELECT -> DELETE를 수행하여 성능 소모 큼
        // wikiPage를 hardDelete 할 일은 많지 않다. 본 서비스가 아티스트나 음향기기 업체 등의 별도 요청으로 인해 hardDelete를 수행해야 하는 경우 정도.
        // 위와 같은 일이 일어나려면 서비스 인지도가 높아야하며, 이는 여러 사람이 이용함을 의미한다. 즉, 이 기능이 실제로 활용될 경우 페이지 리비전 버전이 매우 많을 가능성이 높다.

        pageRevisionCommandService.deleteRevisionDataByWiki_WikiId(wikiId);
        wikiPageCommandService.deleteWikiPageById(wikiId);
    }


    // 유저 정보를 가져오기 위한 메소드
    private UserInfo getUserInfo(String userId) {

        return userInfoQueryService.getUserInfoById(userId);
    }
}
