package com.notfound.lpickbackend.wiki.command.application.service;


import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.AUTO_ENTITIES.Artist;
import com.notfound.lpickbackend.AUTO_ENTITIES.Gear;
import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.temp.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.WikiPageCreateRequestDTO;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * 최상위 엔티티(== 도메인)인 WikiPage 및 해당 엔티티와 연관된 엔티티에 동시에 접근 및 기능 구현하기위한 Service
 * */
@Service
@RequiredArgsConstructor
public class WikiDomainCommandService {

    private final PageRevisionCommandService pageRevisionCommandService;
    private final PageRevisionQueryService pageRevisionQueryService;

    private final WikiPageCommandService wikiPageCommandService;
    private final WikiPageQueryService wikiPageQueryService;

    private final UserInfoQueryService userInfoQueryService;
    private final WikiBookmarkCommandService wikiBookmarkCommandService;

    /** Target : WikiPage, PageRevision
     * Method : POST
     * CRUD : CREATE
     * */
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
    /** Target : WikiPage
     * Method : PATCH
     * CRUD : UPDATE */
    @Transactional
    public void revertWikiPageAndRevision(String wikiId, String targetRevisionId) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);
        PageRevision revertRevision = pageRevisionQueryService.getPageRevisionById(targetRevisionId);

        wikiPage.updateCurrentRevision(revertRevision.getRevisionId());

        wikiPageCommandService.updateWikiPage(wikiPage);
    }

    /** Target : WikiPage, PageRevision, WikiBookmark, Debate, (Artist, Album, Gear)
     * Method : DELETE
     * CRUD : DELETE */
    @Transactional
    public void hardDeleteWikiPage(String wikiId) {

        /** wikiPage와 연관관계를 지니는 자식엔티티 PageRevision을 우선적으로 모두 삭제한 뒤에 WikiPage를 삭제한다. */
        // JPA CASCADE 기반하에 삭제할 수 있지만 다음과 같이 리포지토리에서 연관 데이터 한번에 찾아 삭제하는 방식을 사용한다.
        // CASCADE 방식은 매우 많은 양의 자식엔티티를 삭제해야하는경우 엔티티 하나하나에 전부 SELECT -> DELETE를 수행하여 성능 소모 큼
        // wikiPage를 hardDelete 할 일은 많지 않다. 본 서비스가 아티스트나 음향기기 업체 등의 별도 요청으로 인해 hardDelete를 수행해야 하는 경우 정도.
        // 위와 같은 일이 일어나려면 서비스 인지도가 높아야하며, 이는 여러 사람이 이용함을 의미한다. 즉, 이 기능이 실제로 활용될 경우 페이지 리비전이나 북마크가 매우 많을 가능성이 높다.
        // 성능적으로 손해를 보일 수 있는 PageRevision, WikiBookmark에 대해서는 bulk delete를 진행한다.

        WikiPage targetWikiPage = wikiPageQueryService.getWikiPageById(wikiId); // cascade 및 연관 엔티티 null 처리 목적으로 read 진행

        // 별도 save 하지않아도 영속성 컨텍스트에서 이미 관리되도록 본 트랜잭션 내에서 끌어와졌으니 상관 X
        Artist artist = targetWikiPage.getArtist();
        Album album = targetWikiPage.getAlbum();
        Gear gear = targetWikiPage.getGear();

        if(artist != null) artist.setWiki(null);
        if(album != null) album.setWiki(null);
        if(gear != null) gear.setWiki(null);


        // bulk delete 수행
        pageRevisionCommandService.deleteRevisionDataByWiki_WikiId(wikiId);
        wikiBookmarkCommandService.deleteAllBookmarkDataByWiki_WikiId(wikiId);
        // Debate는 Cascade 기반 연계 삭제 수행. DebateChat에 대한 bulk delete 구현은 Debate 관련 진행하며 구현 예정.
        // 삭제 대상에 대한 삭제 수행.
        wikiPageCommandService.deleteWikiPageById(wikiId);
    }

    // 유저 정보를 가져오기 위한 메소드
    // security 병합 후 삭제 예정
    private UserInfo getUserInfo(String userId) {
        return userInfoQueryService.getUserInfoById(userId);
    }
}
