package com.notfound.lpickbackend.UserInfo.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.UserInfo.Query.Repository.UserInfoQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserInfoQueryService {

    private final UserInfoQueryRepository userInfoQueryRepository;

    public UserInfo getUserInfoById(String userId) {
        return userInfoQueryRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));
    }


}
