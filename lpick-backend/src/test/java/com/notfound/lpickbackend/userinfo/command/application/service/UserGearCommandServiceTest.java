package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import com.notfound.lpickbackend.userinfo.query.service.UserGearQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserGearCommandServiceTest {

    @Mock
    private UserGearQueryService userGearQueryService;

    @InjectMocks
    private UserGearCommandService userGearCommandService;


    private UserGear mockUserGear;

    private final GearClass className = GearClass.TURNTABLE;

    private final String oauthId1 = "oauth123";
    private final String oauthId2 = "oauth222";
    private final String userGearId = "1";

    @BeforeEach
    void setUp() {
        UserInfo mockUser1 = UserInfo.builder()
                .oauthId(oauthId1) // 전치사로 OAuthType 추가
                .nickname("")
                .profile("")
                .point(0)
                .stackPoint(0)
                .about("")
                .lpti("")
                .tier(null)
                .build();

        UserInfo mockUser2 = UserInfo.builder()
                .oauthId(oauthId2) // 전치사로 OAuthType 추가
                .nickname("")
                .profile("")
                .point(0)
                .stackPoint(0)
                .about("")
                .lpti("")
                .tier(null)
                .build();

        com.notfound.lpickbackend.servicedata.command.application.domain.GearClass TURNTABLE =
                com.notfound.lpickbackend.servicedata.command.application.domain.GearClass.builder()
                        .className(GearClass.TURNTABLE.name())
                        .build();

        Gear mockGear =  Gear.builder()
                .eqClass(TURNTABLE)
                .build();

        mockUserGear = UserGear.builder()
                .userGearId(userGearId)
                .eq(mockGear)
                .oauth(mockUser1)
                .isFavorite(false)
                .build();
    }


    @Test
    void deleteUserGear() {
        // User1의 userGear에 대해 접근
        given(userGearQueryService.findById(userGearId)).willReturn(mockUserGear);

        // 접근시도 인원은 User2므로 에러 발생
        CustomException exception = assertThrows(CustomException.class, () -> {
            userGearCommandService.deleteUserGear(oauthId2, userGearId);
        });


        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
    }

//    @Test
//    void patchUserGearFavoriteToggle_IfDifferentUserRequested() {
//        given(userGearQueryService.findById(userGearId)).willReturn(mockUserGear);
//
//        // 접근시도 인원은 User2므로 에러 발생
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            userGearCommandService.patchUserGearFavoriteToggle(oauthId2, userGearId); // 여기에서는 NullPointException 발생
//        });
//
//
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
//    }

    @Test
    void patchUserGearFavoriteToggle_IfAlreadyExsitsFavoriteGear() {

        given(userGearQueryService.findGearDetailInfoById(userGearId)).willReturn(mockUserGear);
        given(userGearQueryService.countUserGearFavoriteByClassName(oauthId1, className.name())).willReturn(1L);

        // 접근시도 인원은 User2므로 에러 발생
        CustomException exception = assertThrows(CustomException.class, () -> {
            userGearCommandService.patchUserGearFavoriteToggle(oauthId1, userGearId);
        });


        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FULL_FAVORITE_GEAR);

    }
}