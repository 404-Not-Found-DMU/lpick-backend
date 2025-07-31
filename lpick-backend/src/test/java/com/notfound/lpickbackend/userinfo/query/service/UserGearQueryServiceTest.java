package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.userinfo.command.application.domain.embed.MyPagePrivacySetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.UserGearCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserGearQueryServiceTest {

    @Mock
    private UserSettingQueryService userSettingQueryService;

    @InjectMocks
    private UserGearQueryService userGearQueryService;

    private final String oauthId = "oauth123";

    private UserSetting userSetting;

    @BeforeEach
    void setUp() {
        UserInfo mockUser = UserInfo.builder()
                .oauthId(oauthId) // 전치사로 OAuthType 추가
                .nickname("")
                .profile("")
                .point(0)
                .stackPoint(0)
                .about("")
                .lpti("")
                .tier(null)
                .build();

        userSetting = UserSetting.builder()
                .myPagePrivacySetting(MyPagePrivacySetting.builder()
                        .allowViewGear(false)
                        .build())
                .build();


    }

    @DisplayName("getUserOwnedGearByUserIdWhenUserWantBlind : 마이페이지 소유자가 본인 기기 공유를 false로 설정한 경우 null을 반환함을 검증한다.")
    @Test
    void getUserOwnedGearByUserIdWhenUserWantBlind() {
        given(userSettingQueryService.findById(oauthId)).willReturn(userSetting);

        BlindableResponse<UserGearCollectionResponse> testResult
                = userGearQueryService.getUserOwnedGearByUserId(oauthId);

        assertEquals(testResult, BlindableResponse.of(
                false, null));

    }
}