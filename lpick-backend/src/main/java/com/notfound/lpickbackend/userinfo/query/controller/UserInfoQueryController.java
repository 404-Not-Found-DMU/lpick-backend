package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserInfoResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "유저 정보 조회 컨트롤러", description = "로그인 성공시 유저 정보 조회 등 유저의 개인정보 조회를 담당하는 컨트롤러")
@RequiredArgsConstructor
public class UserInfoQueryController {

    private final UserInfoQueryService userInfoQueryService;

    @GetMapping("/user-info")
    @Operation(summary = "로그인 성공 후 정보 조회", description = "user 고유 id, nickname 등 필요한 정보를 포함합니다.")
    ResponseEntity<UserInfoResponse> readUserInfo(
            @AuthenticationPrincipal OAuth2UserDetails userDetail
            ) {

        return ResponseEntity.ok(userInfoQueryService.getUserInfo(userDetail.getUsername()));
    }
}
