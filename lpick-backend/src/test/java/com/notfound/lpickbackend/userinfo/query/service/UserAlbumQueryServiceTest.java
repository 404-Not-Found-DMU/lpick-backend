package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.userinfo.command.application.domain.embed.MyPagePrivacySetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedHeader;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAlbumQueryServiceTest {


    @Mock
    private UserSettingQueryService userSettingQueryService;

    @InjectMocks
    private UserAlbumQueryService userAlbumQueryService;

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
                        .allowViewCollection(false)
                        .build())
                .build();


    }

    @DisplayName("getUserAlbumListByOauthIdWhenUserWantBlind : 마이페이지 소유자가 앨범 목록 공유를 false로 설정한 경우 null을 반환함을 검증한다.")
    @Test
    void getUserAlbumListByOauthIdWhenUserWantBlind() {
        given(userSettingQueryService.findById(oauthId)).willReturn(userSetting);

        BlindableResponse<Page<UserAlbumOwnedHeader>> testResult
                = userAlbumQueryService.getUserAlbumListByOauthId(
                        oauthId, PageRequest.of(1, 10));

        assertEquals(testResult, BlindableResponse.of(
                false, null));
    }
}