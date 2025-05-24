package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.command.application.service.PageRevisionCommandService;
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
     * DB에 더미데이터 PageRevision을 만들기 위한 기능.
     *
     * @param request - 리비전을 등록할 위키문서의 id, 위키문서 내역 content를 지니는 class
     * @param dummyUserId - 사용자의 primary key인 oauthId를 기입한다.
     * @return PageRevisionResponse를 반환한다.
     * */
    // post임에도 requestParam이 쓰인이유는, SpringSecurity 기반 적용 되지 않았기 때문.
    @PostMapping("/page-revision")
    public ResponseEntity<PageRevisionResponse> createPageRevision(@RequestBody PageRevisionRequest request,
                                                                   @RequestParam("dummyUserId") String dummyUserId) {

        UserInfo user = userInfoQueryService.getUserInfoById(dummyUserId);

        PageRevisionResponse newRevision = pageRevisionCommandService.createNewRevision(request, user);

        return ResponseEntity.status(HttpStatus.OK).body(newRevision);
    }
}
