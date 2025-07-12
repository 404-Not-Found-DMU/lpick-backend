package com.notfound.lpickbackend.userinfo.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserAboutEditRequest {
    @NotBlank
    private String about; // 사용자 자기소개 설명문
}
