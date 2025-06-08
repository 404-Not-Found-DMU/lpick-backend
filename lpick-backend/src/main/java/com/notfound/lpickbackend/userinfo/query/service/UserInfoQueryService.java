package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoQueryService {

    private final UserInfoQueryRepository userInfoQueryRepository;

    public UserInfo getUserInfoById(String userId) {
        return userInfoQueryRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));
    }


}
