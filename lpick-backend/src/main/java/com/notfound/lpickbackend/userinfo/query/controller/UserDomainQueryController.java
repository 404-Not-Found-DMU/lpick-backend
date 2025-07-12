package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserActivityResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserSettingResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserDomainQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserSettingQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserDomainQueryController {

    private final UserDomainQueryService userDomainQueryService;
    private final UserSettingQueryService userSettingQueryService;


    @GetMapping("/user/activity-count")
    public ResponseEntity<UserActivityResponse> getUserActivityCount() {
        return ResponseEntity.ok().body(userDomainQueryService.getUserActivityCount(UserInfoUtil.getOAuthId()));
    }

    @GetMapping("/user/setting")
    public ResponseEntity<UserSettingResponse> getUserSetting() {
        return ResponseEntity.ok().body(userSettingQueryService.getUserSetting(UserInfoUtil.getOAuthId()));
    }

}
