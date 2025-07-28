package com.notfound.lpickbackend.userinfo.query.dto.response;

import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.PageThemeSetting;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserNotificationSettingDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserPrivacySettingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSettingResponse {

    private final UserPrivacySettingDTO privacy;

    private final PageThemeSetting theme;

    private final UserNotificationSettingDTO notification;

}
