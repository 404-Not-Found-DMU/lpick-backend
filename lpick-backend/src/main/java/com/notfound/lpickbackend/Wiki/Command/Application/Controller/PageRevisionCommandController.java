package com.notfound.lpickbackend.Wiki.Command.Application.Controller;

import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Request.PageRevisionRequest;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Response.PageRevisionResponse;
import com.notfound.lpickbackend.Wiki.Command.Application.Service.PageRevisionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class PageRevisionCommandController {

    private final PageRevisionCommandService pageRevisionCommandService;

    // Swagger 도입 후에는 java doc 사용 여부 논의 필요할 듯 보임.
    /**
     * DB에 더미데이터 PageRevision을 만들기 위한 기능.
     *
     * @param request - 리비전을 등록할 위키문서의 id, 위키문서 내역 content를 지니는 class
     * @param dummyUserId - 사용자의 primary key인 oauthId를 기입한다.
     * @return PageRevisionResponse를 반환한다.
     * */
    // post임에도 requestParam이 쓰인이유는, SpringSecurity 기반 적용 이후 ContextHolder에서 ID 가져오기 위함. + 아직 사용자 기능 구현 안되어있음.
    @PostMapping("/write-dummydata")
    public ResponseEntity<PageRevisionResponse> dummyPageRevision(@RequestBody PageRevisionRequest request,
                                                                  @RequestParam("dummyUserId") String dummyUserId) {
        PageRevisionResponse newRevision = pageRevisionCommandService.writeNewRevision(request, dummyUserId);

        return ResponseEntity.status(HttpStatus.OK).body(newRevision);
    }
}
