package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserAboutEditRequest;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserSettingEditRequest;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserSettingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDomainCommandService {
    private final UserInfoCommandService userInfoCommandService;
    private final UserInfoQueryService userInfoQueryService;
    private final UserSettingCommandService userSettingCommandService;
    private final UserSettingQueryService userSettingQueryService;

    @Transactional
    public void editUserAbout(String oauthId, UserAboutEditRequest aboutEditRequest) {
        UserInfo targetUser = userInfoQueryService.getUserInfoById(oauthId);
        targetUser.editAbout(aboutEditRequest.getAbout());
        userInfoCommandService.saveUserInfo(targetUser);
    }

    @Transactional
    public void editUserSetting(String oauthId, UserSettingEditRequest settingEditRequest) {
        UserSetting userSetting = userSettingQueryService.findById(oauthId);
        userSetting.updateSettingByRequest(settingEditRequest);
        userSettingCommandService.saveUserSetting(userSetting);
    }
}
