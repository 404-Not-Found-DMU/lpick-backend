package com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request;

import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.PageThemeSetting;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSettingEditRequest {
    // edit request로 올리는 내역은 그 자체로 수정할 일 X -> final로 설정.

    @Valid
    @NotNull(message = "privacy 설정은 필수입니다")
    private final UserPrivacySettingDTO privacy;

    @NotNull(message = "테마 색상을 선택해주세요")
    private final PageThemeSetting theme;

    @Valid
    @NotNull(message = "notification 설정은 필수입니다")
    private final UserNotificationSettingDTO notification;
}
