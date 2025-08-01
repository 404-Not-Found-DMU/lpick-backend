package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.common._super.BaseQueryService;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.GearClass;
import com.notfound.lpickbackend.servicedata.query.repository.GearClassQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


// GearClass 엔티티는 기존과 다른 양식을 취하므로 BaseEntity, BaseQueryService 활용하지 않음.
@Service
@RequiredArgsConstructor
public class GearClassQueryService {

    private final GearClassQueryRepository gearClassQueryRepository;

    // GearClass는 name 또한 기능
    public GearClass findByClassName(String className) {
        return gearClassQueryRepository.findById(className)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GEAR));
    }
}
