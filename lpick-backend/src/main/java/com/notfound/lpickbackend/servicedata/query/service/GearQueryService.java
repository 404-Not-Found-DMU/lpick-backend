package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.common._super.BaseQueryService;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.query.repository.GearQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GearQueryService extends BaseQueryService<Gear, String> {
    private final GearQueryRepository gearQueryRepository;

    @Override
    protected JpaRepository<Gear, String> getRepository() {
        return gearQueryRepository;
    }

    @Override
    protected ErrorCode notFoundErrorCode() {
        return ErrorCode.NOT_FOUND_GEAR;
    }
    
    // 이 사이에 개발자가 추가하는 메소드가 오게끔 하기
    
    @Override
    public Gear findById(String id) {
        return super.findById(id);
    }

    @Override
    public List<Gear> findAll() {
        return super.findAll();
    }
}
