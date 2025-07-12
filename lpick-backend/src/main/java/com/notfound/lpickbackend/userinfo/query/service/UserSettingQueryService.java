package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserSettingResponse;
import com.notfound.lpickbackend.userinfo.query.repository.UserSettingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingQueryService {

    private final UserSettingQueryRepository userSettingQueryRepository;


    @Transactional(readOnly = true)
    public UserSettingResponse getUserSetting(String oauthId) {
        UserSetting userSetting = findById(oauthId);

        if(!(userSetting.getOauthId().equals(UserInfoUtil.getOAuthId())))
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS); // 사용자 설정은 설령 admin이더라도 다른 인원이 건들 수 없어야 하므로 검증

        return userSetting.toDTO();
    }


    public UserSetting findById(String oauthId) {
        return userSettingQueryRepository.findById(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)); // UserSetting은 사용자 생성 시 기본적으로 동시에 생성되는 일대일 엔티티.
    }
}

