package com.notfound.lpickbackend.userinfo.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LogoutRequestDTO {

    private String accessToken;
    private String oAuthId;
}
