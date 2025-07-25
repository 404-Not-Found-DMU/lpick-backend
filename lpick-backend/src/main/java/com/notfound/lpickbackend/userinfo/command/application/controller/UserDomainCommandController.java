package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserAboutEditRequest;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserSettingEditRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserDomainCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserInfoComandControler(== OAuth 기반 기능)와 웹 페이지 내에서의 사용자 기능 관련
 * 분할을 위한 Controller의 구분.
 * 해당 Controller는 사용자의 프로필이나 설정 등, '서비스' 내에서 동작할 요청들의 집합입니다.
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "서비스 내 사용자 관련 CUD 컨트롤러", description = "사용자의 세팅이나 프로필 등의 수정을 담당.")
public class UserDomainCommandController {

    private final UserDomainCommandService userDomainCommandService;

    // userInfo가 지니는 about을 수정.
    @PatchMapping("/user/about")
    @Operation(summary = "사용자 자기소개 수정", description = "마이페이지에 표기되는 사용자 자기소개 수정")
    public ResponseEntity<SuccessCode> editUserAbout(
            @RequestBody @Valid UserAboutEditRequest aboutEditRequest
    ) {
        userDomainCommandService.editUserAbout(UserInfoUtil.getOAuthId(), aboutEditRequest);

        return ResponseEntity.ok(SuccessCode.USER_ABOUT_UPDATE_SUCESS);
    }

    @PatchMapping("/user/setting")
    @Operation(summary = "사용자 설정 수정", description = "사용자의 setting 수정(마이페이지 표기여부, 테마, 실시간 알림 여부)")
    public ResponseEntity<SuccessCode> editUserSetting(
            @RequestBody @Valid UserSettingEditRequest settingEditRequest
    ) {
        userDomainCommandService.editUserSetting(UserInfoUtil.getOAuthId(), settingEditRequest);

        return ResponseEntity.ok(SuccessCode.USER_SETTING_UPDATE_SUCESS);
    }
}
