package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.userinfo.command.application.service.OAuth2UserCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserCommandController {

    OAuth2UserCommandService userCommandService;

//    @GetMapping("/login/oauth2/code/kakao")
//    ResponseEntity<?> oAuth2CodeKakao(@RequestParam("code") String code) {
//
//        log.info("Kakao code: {}", code);
//
//        // Mono : Spring WebFlux에서 사용하는 비동기 처리를 위한 타입
//        Mono<KakaoTokenResponse> kakaoAccessToken = userCommandService.getKakaoAccessToken(code);
//
//        log.warn("access token: {}", kakaoAccessToken);
//
//        return ResponseEntity.ok(kakaoAccessToken.toString());
//    }
}
