package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.util.EnumUtils;
import com.notfound.lpickbackend.servicedata.query.service.GearQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserGearPostRequest;
import com.notfound.lpickbackend.userinfo.command.repository.UserGearCommandRepository;
import com.notfound.lpickbackend.userinfo.query.dto.response.FavoriteToggleStatus;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserGearQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGearCommandService {
    private final UserGearCommandRepository userGearCommandRepository;
    private final UserGearQueryService userGearQueryService;
    private final GearQueryService gearQueryService;
    private final UserInfoQueryService userInfoQueryService;

    // userGear는 분류별로 제한 없이 설정할 수 있다
    // userGear는 favorite toggle이 존재하며, 분류 별로 1개씩의 favorite만 설정해 둘 수 있다

    @Transactional
    public void createNewUserGear(String oAuthId, UserGearPostRequest userGearPostRequest) {
        UserGear userGear = UserGear.builder()
                .userGearId(null)
                .eq(gearQueryService.findById(userGearPostRequest.getGearId()))
                .oauth(userInfoQueryService.getUserInfoById(oAuthId))
                .build();

        userGearCommandRepository.save(userGear);
    }

    @Transactional
    public void deleteUserGear(String oAuthId, String userGearId) {
        UserGear userGear = userGearQueryService.findById(userGearId);

        // 사용자 소유의 userGear가 맞는지 확인
        checkUserOwnedGear(userGear, oAuthId);

        userGearCommandRepository.delete(userGear);
    }

    @Transactional
    public FavoriteToggleStatus patchUserGearFavoriteToggle(String oAuthId, String userGearId) {
        // userGear는 분류별로 1개의 favorite만 설정할 수 있다

        UserGear target = userGearQueryService.findGearDetailInfoById(userGearId);

        checkUserOwnedGear(target, oAuthId);

        long classFavoriteCount =
                userGearQueryService.countUserGearFavoriteByClassName(oAuthId,
                        target.getEq().getEqClass().getClassName()
                );
        
        // 분류 카운트가 1개 이상이고, 토글 결과가 true인 경우 에러 발생
        if(classFavoriteCount >= 1L && !target.isFavorite())
            throw new CustomException(ErrorCode.ALREADY_FULL_FAVORITE_GEAR);

        target.setFavorite(!target.isFavorite());

        userGearCommandRepository.save(target);

        return new FavoriteToggleStatus(target.isFavorite());
    }

    /** pathvariable 기반 ID로 가져온 엔티티와 현재 security 기반하에 사용자가 동일한지 검증  */
    protected void checkUserOwnedGear(UserGear userGear, String oAuthId) {
        if(!userGear.getOauth().getOauthId().equals(oAuthId)) throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);

    }
}
