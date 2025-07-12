package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.request.UserAboutEditRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserDomainCommandService;
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
public class UserDomainCommandController {

    private final UserDomainCommandService userDomainCommandService;

    // userInfo가 지니는 about을 수정.
    @PatchMapping("/user/about")
    public ResponseEntity<SuccessCode> editUserAbout(
            @RequestBody @Valid UserAboutEditRequest aboutEditRequest
    ) {
        userDomainCommandService.editUserAbout(UserInfoUtil.getOAuthId(), aboutEditRequest);

        return ResponseEntity.ok(SuccessCode.USER_ABOUT_UPDATE_SUCESS);
    }

}
