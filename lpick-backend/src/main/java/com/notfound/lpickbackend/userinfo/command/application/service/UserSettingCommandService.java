package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.command.repository.UserSettingCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingCommandService {

    private final UserSettingCommandRepository userSettingCommandRepository;


    public void saveUserSetting(UserSetting setting) {
        userSettingCommandRepository.save(setting);
    }
}
