package com.notfound.lpickbackend.userinfo.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/*
* 생성된 Token을 전달하는 DTO
* */
public class TokenResponseDTO {

    String accessToken;
    String refreshToken;
}
