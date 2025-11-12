package com.notfound.lpickbackend.common._event.point.service;


import com.notfound.lpickbackend.common._event.point.ActivityType;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.tier.query.repository.TierCommandRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.Tier;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointAccrualService {
    private final UserInfoCommandRepository userRepository;
    private final TierCommandRepository tierRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void accrue(String userId, ActivityType activity, String sourceId) {

        UserInfo user = userRepository.findByOauthId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));

        log.info("서비스 호출은 됐네요");
        
        Integer point = activity.getPoint();

        log.info("{} ::  {}", userId, point);
        userRepository.addPoint(userId, point);


        // 등급 계산 + Redis user:cv:{userId} INCR 는 필요 시 여기서
    }

    /** 현재 stack된 point 양이 승급할 정도의 양인지 평가하기 위한 내부 메소드 */
//    private boolean isUserAdvancementPoint(UserInfo userInfo) {
//        List<Tier> tierList = tierRepository.findAll();
//        tierList.get(1).getName()
//        switch(userInfo.getTier().getName()) {
//            case Tier.
//        }
//
//    }
}
