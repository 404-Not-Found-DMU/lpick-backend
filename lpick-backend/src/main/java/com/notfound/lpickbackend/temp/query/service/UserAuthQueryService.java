package com.notfound.lpickbackend.temp.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserAuth;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.temp.command.domain.UserAuthentication;
import com.notfound.lpickbackend.temp.query.repository.UserAuthQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 서버에서 '내부적으로' 검증 필요시 활용하기 위한 서비스.
 * 요청에 대한 사용자 권한 검증은 JWT 내의 Tier, Auth를 기반으로 SpringSecurity 기반 검증 예정.
 * */
@Service
@RequiredArgsConstructor
public class UserAuthQueryService {
    private final UserAuthQueryRepository userAuthQueryRepository;

    // 검증 Enum name 목록 : MEDIATOR(중재자 - 토론 한정), MANAGER(매니저 - 커뮤니티 한정), ADMIN(본 서비스 개발자 등급)

    /** 사용자의 Role이 ADMIN인지 검증. */
    public void requireMediator(String oauthId) {
        UserAuth userAuth = userAuthQueryRepository.findByOauth_OauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        // MEDIATOR나 ADMIN이 아니면 예외 발생 X
        if(!UserAuthentication.MEDIATOR.name().equals(userAuth.getAuth().getName())
                && !UserAuthentication.ADMIN.name().equals(userAuth.getAuth().getName()))
            throw new CustomException(ErrorCode.INSUFFICIENT_ACCESS_LEVEL);
    }

    /** 사용자의 Role이 ADMIN인지 검증. */
    public void requireManager(String oauthId) {
        UserAuth userAuth = userAuthQueryRepository.findByOauth_OauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        // MANAGER나 ADMIN이 아니면 예외 발생
        if(!UserAuthentication.MANAGER.name().equals(userAuth.getAuth().getName())
                && !UserAuthentication.ADMIN.name().equals(userAuth.getAuth().getName()))
            throw new CustomException(ErrorCode.INSUFFICIENT_ACCESS_LEVEL);
    }

    /** 사용자의 Role이 ADMIN인지 검증. */
    public void requireAdmin(String oauthId) {
        UserAuth userAuth = userAuthQueryRepository.findByOauth_OauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        // ADMIN이 아니면 예외 발생
        if(!UserAuthentication.ADMIN.name().equals(userAuth.getAuth().getName()))
            throw new CustomException(ErrorCode.INSUFFICIENT_ACCESS_LEVEL);
    }

}
