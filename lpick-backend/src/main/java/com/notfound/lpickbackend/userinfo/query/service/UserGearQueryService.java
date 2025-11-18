package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClassEnum;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.UserGearCollectionResponse;
import com.notfound.lpickbackend.userinfo.query.repository.UserGearQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGearQueryService {
    private final UserGearQueryRepository userGearQueryRepository;
    private final UserSettingQueryService userSettingQueryService;

    public BlindableResponse<UserGearCollectionResponse> getUserOwnedGearByUserId(String userId) {

        UserSetting userSetting = userSettingQueryService.findById(userId);

        if(!userSetting.getMyPagePrivacySetting().isUserAllowViewCollection())
            return BlindableResponse.of(
                    userSetting.getMyPagePrivacySetting().isUserAllowViewCollection(),
                    null
            );

        return BlindableResponse.of(
                userSetting.getMyPagePrivacySetting().isUserAllowViewCollection(),
                getUserOwnedGear(userId)
        );
    }


    /** 마이페이지 - 각 기어 표기. favoirte가 없으면 제일 마지막에 선택한 기기, favorite가 있으면 해당 기기를 favorite */
    public UserGearCollectionResponse getUserOwnedGear(String oAuthId) {
        List<GearInfoResponse> list = userGearQueryRepository.findAllGearCollectionByUserId_V1(oAuthId);

        GearInfoResponse speaker   = null;
        GearInfoResponse headphone = null;
        GearInfoResponse turntable = null;

        // DB 내에 잘못된 대상이 있는경우 어떻게 처리해야하는가?
        for (GearInfoResponse info : list) {
            switch (info.getGearClass()) {
                case SPEAKER -> speaker = info;
                case HEADPHONE -> headphone = info;
                case TURNTABLE -> turntable = info;
                default -> throw new CustomException(ErrorCode.ILLEGAL_VALUE_DETECTED);
            }
        }

        return UserGearCollectionResponse.builder()
                .userId(oAuthId)
                .ownedHeadPhone(headphone)
                .ownedSpeaker(speaker)
                .ownedTurnTable(turntable)
                .build();
    }

    /** UserGear는 User - Gear 간의 매핑 여부 및 좋아요 여부만을 확인하므로, UserGear의 분류 등을 확인하기 위해서는 해당 기능 사용 요망. EntityGraph 적용된 엔티티 반환 */
    public UserGear findGearDetailInfoById(String userGearId) {
        return userGearQueryRepository.findGearDetailByIdWithEqAndEqClass(userGearId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_GEAR));
    }


    public UserGear findById(String id) {
        return userGearQueryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_GEAR));
    }

    /** 어떤 사용자가 특정 ClassName의 UserGear를 얼마나 Favorite True 했는지 카운트하는 목적의 메소드 */
    @Transactional(readOnly = true)
    public long countUserGearFavoriteByClassName(String oAuthId, String className) {
        return userGearQueryRepository.countFavoritesByOauthIdAndClassName(oAuthId, className);
    }

    public UserGear findByIdAndOAuth_OAuthId(String userGearId, String oauthId) {
        return userGearQueryRepository.findByUserGearIdAndOauth_OauthId(userGearId, oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_GEAR));
    }

    public List<GearInfoResponse> getUserOwnedGearListByGearClass(String oauthId, GearClassEnum gearClass) {
        return userGearQueryRepository.findAllUserGearListByUserIdAndGearClass_V1(oauthId, gearClass.name());
    }
}
