package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserActivityResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserProfileResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserSettingResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserDomainQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserSettingQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
@Tag(name = "서비스 내 사용자 대상 조회 컨트롤러", description = "회원가입, 로그인 등이 아닌 서비스 내 사용자 관련 api 모음")
public class UserDomainQueryController {

    private final UserDomainQueryService userDomainQueryService;
    private final UserSettingQueryService userSettingQueryService;


    @GetMapping("/user/activity-count")
    @Operation(summary = "요청 당사자의 커뮤니티/위키 활동 횟수 조회", description = "게시글 작성, 댓글 작성, 위키 수정, 토론 참여 횟수 제공")
    public ResponseEntity<UserActivityResponse> getUserActivityCount(
            @AuthenticationPrincipal OAuth2UserDetails userDetail
            ) {
        return ResponseEntity.ok().body(userDomainQueryService.getUserActivityCount(userDetail.getUsername()));
    }

    @GetMapping("/user/setting")
    @Operation(summary = "요청 당사자의 서비스 내 설정 내역 조회", description = "마이페이지 내 특정 내역 표기 여부/페이지 테마/실시간 알림여부")
    public ResponseEntity<UserSettingResponse> getUserSetting(
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        return ResponseEntity.ok().body(userSettingQueryService.getUserSetting(userDetail.getUsername()));
    }

//    @GetMapping("/user/profile")
//    @Operation(summary = "요청 당사자의 마이페이지 프로필 내역 조회", description = "마이페이지 내 누적포인트, 한계표인트, 현재 등급, 자기소개 제공")
//    public ResponseEntity<UserProfileResponse> getUserProfile(
//            @AuthenticationPrincipal OAuth2UserDetails userDetail
//    ) {
//        return ResponseEntity.ok().body(userDomainQueryService.getUserProfile(userDetail.getUsername()));
//    }


    @GetMapping("/my-page/{oauthId}/activity-count")
    @Operation(summary = "대상 사용자의 커뮤니티/위키 활동 횟수 조회", description = "설정에 따라 표기되지 않을 수 있음. 게시글 작성, 댓글 작성, 위키 수정, 토론 참여 횟수 제공")
    public ResponseEntity<BlindableResponse<UserActivityResponse>> getUserActivityCountByOauthId(
            @PathVariable("oauthId") String oauthId
    ) {
        return ResponseEntity.ok().body(userDomainQueryService.getUserActivityCountByOauthId(oauthId));
    }

}
