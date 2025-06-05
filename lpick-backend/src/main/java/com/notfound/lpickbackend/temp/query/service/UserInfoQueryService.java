package com.notfound.lpickbackend.temp.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.temp.query.repository.UserInfoQueryRepository;
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
