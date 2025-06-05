package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.command.application.service.PageRevisionCommandService;
import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class PageRevisionCommandController {

    private final PageRevisionCommandService pageRevisionCommandService;
    
    // 추후삭제 : spring security 기반의 context-holder 통한 사용자 정보 불러오기 구현 이후 삭제해야할 대상입니다.
    private final UserInfoQueryService userInfoQueryService;

    // Swagger 도입 후에는 java doc 사용 여부 논의 필요할 듯 보임.
    /**
     * 이미 존재하는 WikiPage에 대해 새 PageRevision을 만드는 경우 사용.
     * 사용자기준, 위키 문서를 수정하는 경우이나 PageRevision은 Update가 아닌 새 버전을 Create 하는 방식이므로 본 요청 사용
     *
     * @param request - 리비전을 등록할 위키문서의 id, 위키문서 내역 content를 지니는 class
     * @param dummyUserId - 사용자의 primary key인 oauthId를 기입한다.
     * @return PageRevisionResponse를 반환한다.
     * */
    // post임에도 requestParam이 쓰인이유는, SpringSecurity 기반 적용 되지 않았기 때문.
    @PostMapping("/wiki/{wikiId}/revision")
    public ResponseEntity<PageRevisionResponse> createPageRevision(
            @RequestBody PageRevisionRequest request,
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String dummyUserId
    ) {

        UserInfo user = userInfoQueryService.getUserInfoById(dummyUserId);

        PageRevisionResponse newRevision = pageRevisionCommandService.createNewRevision(request, wikiId, user);

        return ResponseEntity.status(HttpStatus.OK).body(newRevision);
    }

    // PageRevision은 Update 될 일이 없는 버전 누적형식 데이터로 UpdateMapping 생략

    // PageRevision은 일부만 삭제되어서는 안된다.'반달 행위' 검증 목적.
    // 반달 == 도배, 뻘글테러, 문서 조작 등의 문제 행위를 총칭하여 일컫는 말.
    // 어떤 사용자가 반달행위를 하였음을 입증하기위해서는 문제가되는 PageRevision이 남아있어야하므로, 개별삭제는 불가
    // BLIND나 DELETE와 같이 위키문서 전체에 대해 '서비스 운영진'의 삭제 조치가 행해지는 경우가 아래의 DELETE 요청.
    // @PreAuthorize("hasAuthority('AUTH_ADMIN')")
    @DeleteMapping("/wiki/{wikiId}/revision")
    public ResponseEntity<Void> deletePageRevisionData(
            @PathVariable("wikiId") String wikiId
    ) {
        pageRevisionCommandService.deleteRevisionDataByWiki_WikiId(wikiId);

        return ResponseEntity.noContent().build();
    }

}
