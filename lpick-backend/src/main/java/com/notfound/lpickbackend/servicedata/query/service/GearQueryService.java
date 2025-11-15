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
    


}
