package com.notfound.lpickbackend.userInfo.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserAuth;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userInfo.command.domain.UserAuthentication;
import com.notfound.lpickbackend.userInfo.query.repository.UserAuthQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthQueryService {
    private final UserAuthQueryRepository userAuthQueryRepository;


    /** 사용자의 Role이 ADMIN인지 검증. */
    public void requireAdmin(String oauthId) {
        UserAuth userAuth = userAuthQueryRepository.findByOauth_OauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        if(!UserAuthentication.ADMIN.name().equals(userAuth.getAuth().getName()))
            throw new CustomException(ErrorCode.INSUFFICIENT_ACCESS_LEVEL);
    }
}
