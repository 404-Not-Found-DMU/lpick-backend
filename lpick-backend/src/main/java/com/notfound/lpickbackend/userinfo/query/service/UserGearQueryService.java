package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.UserGearCollectionResponse;
import com.notfound.lpickbackend.userinfo.query.repository.UserGearQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    public UserGearCollectionResponse getUserOwnedGear(String oAuthId) {
        List<GearInfoResponse> list = userGearQueryRepository.findAllGearCollectionByUserId(oAuthId);

        GearInfoResponse speaker   = null;
        GearInfoResponse headphone = null;
        GearInfoResponse turntable = null;

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

    public UserGear findById(String id) {
        return userGearQueryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_GEAR));
    }
}
