package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.common._super.BaseQueryService;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.query.repository.GearQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GearQueryService {
    private final GearQueryRepository gearQueryRepository;

    
    // 이 사이에 개발자가 추가하는 메소드가 오게끔 하기
    
    public Gear findById(String id) {
        return gearQueryRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GEAR));
    }

    public List<Gear> findAll() {
        return gearQueryRepository.findAll();
    }
}
