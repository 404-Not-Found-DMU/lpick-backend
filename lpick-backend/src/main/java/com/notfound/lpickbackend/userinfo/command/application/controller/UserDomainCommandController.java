package com.notfound.lpickbackend.userinfo.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserInfoComandControler(== OAuth 기반 기능)와 웹 페이지 내에서의 사용자 기능 관련
 * 분할을 위한 Controller의 구분.
 * 해당 Controller는 사용자의 프로필이나 설정 등, '서비스' 내에서 동작할 요청들의 집합입니다.
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserDomainCommandController {

//    @PostMapping("/user/")

}
