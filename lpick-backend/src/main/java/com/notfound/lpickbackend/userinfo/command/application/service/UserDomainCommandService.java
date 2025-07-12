package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.dto.request.UserAboutEditRequest;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDomainCommandService {
    private final UserInfoCommandService userInfoCommandService;
    private final UserInfoQueryService userInfoQueryService;

    @Transactional
    public void editUserAbout(String oauthId, UserAboutEditRequest aboutEditRequest) {
        UserInfo targetUser = userInfoQueryService.getUserInfoById(oauthId);
        targetUser.editAbout(aboutEditRequest.getAbout());
        userInfoCommandService.saveUserInfo(targetUser);
    }
}
