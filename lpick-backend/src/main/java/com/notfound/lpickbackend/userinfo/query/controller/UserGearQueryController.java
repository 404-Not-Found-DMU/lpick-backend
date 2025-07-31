package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.UserGearCollectionResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserGearQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "사용자 음향기기 조회 컨트롤러", description = "사용자 음향기기 관련 조회")
public class UserGearQueryController {
    private final UserGearQueryService userGearQueryService;

    @GetMapping("/user/gear")
    @Operation(summary = "요청 당사자의 음향기기 확인", description = "확인되는 음향기기는 분류별로 1개만 확인 가능")
    public ResponseEntity<UserGearCollectionResponse> getUserOwnedGearList() {
        return ResponseEntity.ok().body(userGearQueryService.getUserOwnedGear(UserInfoUtil.getOAuthId()));
    }

    @GetMapping("/my-page/{oauthId}/gear")
    @Operation(summary = "대상의 음향기기 확인", description = "음향기기는 분류별로 1개만 확인 가능. 마이페이지나 커뮤니티 내 사용자 설명 표기 등에 사용")
    public ResponseEntity<BlindableResponse<UserGearCollectionResponse>> getUserOwnedGearList(
            @PathVariable("oauthId") String oauthId
    ) {

        return ResponseEntity.ok().body(userGearQueryService.getUserOwnedGearByUserId(oauthId));
    }
}
