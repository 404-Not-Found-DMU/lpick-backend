package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common._aop.point.EarnPoint;
import com.notfound.lpickbackend.common._event.point.ActivityType;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import com.notfound.lpickbackend.security.util.JwtTokenProvider;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.*;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserInfoCommandService extends DefaultOAuth2UserService {

    private final RedisService redisService;
    private final int accessTokenValidity;
    private final int refreshTokenValidity;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoCommandRepository userInfoCommandRepository;
    private final S3Uploader s3Uploader;

    public UserInfoCommandService(
            RedisService redisService,
            @Value("${token.access_token_expiration_time}") int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}") int refreshTokenValidity,
            JwtTokenProvider jwtTokenProvider,
            UserInfoCommandRepository userInfoCommandRepository,
            S3Uploader s3Uploader
    ) {
        this.redisService = redisService;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userInfoCommandRepository = userInfoCommandRepository;
        this.s3Uploader = s3Uploader;
    }


    @EarnPoint(
            activity = ActivityType.WRITE_COMMENT, // 본 어노테이션이 설정된 메소드의 커밋이 완전히 종료된 후 동작
            userId = "#auth?.name ?: #userDetail.oauthId", // 메서드 파라미터 이름 사용(컴파일 옵션 -parameters 필요)
            sourceId = "#result ?: null"        // #result == 본 어노테이션이 붙은 메소드의 리턴값 의미. 단, 모든 구현 사항이 리턴값을 가지지 못할 수 있다.
    )
    public void logout(LogoutRequestDTO logoutRequestDTO) {

        // Token 무효화
        invalidateTokens(logoutRequestDTO.getOAuthId(), logoutRequestDTO.getAccessToken());

    }

    public TokenResponseDTO refresh(TokenRefreshRequestDTO tokenRefreshRequestDTO) {

        String oAuthId = tokenRefreshRequestDTO.getOAuthId();

        // Token 무효화
        invalidateTokens(oAuthId, tokenRefreshRequestDTO.getAccessToken());

        UserInfo userInfo = getUserInfo(oAuthId);

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(oAuthId, userInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuthId, userInfo);

        // redis whiteList에 refreshToken 저장
        redisService.saveWhitelistRefreshToken(oAuthId, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(accessToken, refreshToken, userInfo.getOauthId());
    }

    /*
     * AccessToken은 BlackList에 추가, RefreshToken은 White에서 삭제합니다.
     * RefreshToken 재사용 방지를 위해 AccessToken 및 RefreshToken은 발급과 삭제가 동시에 이루어집니다.
     * 고민점 : logout과 refresh에 필요한 데이터는 OAuthId, AccessToken으로 동일하기에 두 로직에서 사용되는 DTO를 나눌지, 하나로 사용할지 고민
     * */
    private void invalidateTokens(String oAuthId, String accessToken) {

        // whiteList에서 OAuthId로 RefreshToken 삭제
        redisService.deleteRefreshToken(oAuthId);
        // BlackList에 AccessToken 추가
        redisService.saveBlacklistAccessToken(accessToken, accessTokenValidity, TimeUnit.MILLISECONDS);
    }

    /* 개발자용 토큰 생성 api */
    public TokenResponseDTO getDeveloperToken() {

        /* 더미데이터에 존재하는 OAuthId '1' 유저 */
        UserInfo userInfo = userInfoCommandRepository.findByOauthId("1").orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 개발자용 accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createDevAccessToken("1", userInfo);
        String refreshToken = jwtTokenProvider.createDevRefreshToken("1", userInfo);

        // redis whiteList에 refreshToken 1년 동안 저장
        redisService.saveWhitelistRefreshToken("1", refreshToken, 365, TimeUnit.DAYS);

        return new TokenResponseDTO(accessToken, refreshToken, userInfo.getOauthId());
    }

    public void saveUserInfo(UserInfo userInfo) {
        userInfoCommandRepository.save(userInfo);
    }

    @Transactional
    public void userRegistration(String oAuthId, UserRegistrationRequest userInfo, MultipartFile profileImage) {

        String imageUrl = "";

        try {

            imageUrl = s3Uploader.upload(profileImage, "USER-INFO");

            UserInfo user = getUserInfo(oAuthId);

            user.registration(userInfo, imageUrl);

        } catch (Exception e) {
            // 보상 삭제
            if (!imageUrl.isEmpty()) {
                s3Uploader.deleteByUrl(imageUrl);
            }

            throw new CustomException(ErrorCode.USER_REGISTRATION_FAIL);
        }

    }

    @Transactional
    public void userUpdate(String oAuthId, UserUpdateRequest userInfo, MultipartFile profileImage) {

        String imageUrl = "";

        try {

            imageUrl = s3Uploader.upload(profileImage, "USER-INFO");

            UserInfo user = getUserInfo(oAuthId);

            // 보상삭제
            if(!imageUrl.isEmpty()) {
                s3Uploader.deleteByUrl(user.getProfile());
            }

            user.update(userInfo, imageUrl);

        } catch (Exception e) {
            // 보상 삭제
            if (!imageUrl.isEmpty()) {
                s3Uploader.deleteByUrl(imageUrl);
            }

            throw new CustomException(ErrorCode.USER_REGISTRATION_FAIL);
        }

    }

    private UserInfo getUserInfo(String oAuthId) {
        return userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    @Transactional
    public void deleteUserInfo(String oAuthId) {

        UserInfo userInfo = getUserInfo(oAuthId);

        userInfo.deleteUserInfo();
    }

    @Transactional
    public void updateLPTI(String lpti) {

        String oauthId = UserInfoUtil.getOAuthId();

        UserInfo userInfo = getUserInfo(oauthId);

        userInfo.updateLPTI(lpti);
    }
}
