package com.notfound.lpickbackend.userinfo.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
/*
* LogoutRequestDTO와 내용은 동일하지만 요청의 목적이 다르기에 가시성 + 유지보수성을 위해 별도의 DTO로 분리
* */
public class TokenRefreshRequestDTO {

    private String oAuthId;
    private String accessToken;
}
