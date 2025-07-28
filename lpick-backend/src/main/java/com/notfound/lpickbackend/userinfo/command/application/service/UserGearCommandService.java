package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.service.GearQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserGearPostRequest;
import com.notfound.lpickbackend.userinfo.command.repository.UserGearCommandRepository;
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
        if(!userGear.getOauth().getOauthId().equals(oAuthId)) throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);

        userGearCommandRepository.delete(userGear);
    }
}
