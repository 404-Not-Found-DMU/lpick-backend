package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.query.repository.GearQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GearQueryService {
    private final GearQueryRepository gearQueryRepository;

    @Transactional(readOnly = true)
    public Gear findById(String gearId) {
        return gearQueryRepository.findById(gearId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GEAR));
    }
}
