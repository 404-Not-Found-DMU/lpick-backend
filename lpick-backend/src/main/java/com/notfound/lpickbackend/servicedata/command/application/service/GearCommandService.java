package com.notfound.lpickbackend.servicedata.command.application.service;

import com.notfound.lpickbackend.common._super.BaseCommandService;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.command.application.domain.dto.TempGearRequest;
import com.notfound.lpickbackend.servicedata.command.application.repository.GearCommandRepository;
import com.notfound.lpickbackend.servicedata.query.service.GearClassQueryService;
import com.notfound.lpickbackend.servicedata.query.service.GearQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GearCommandService extends BaseCommandService<Gear, String> {

    private final GearCommandRepository gearCommandRepository;

    private final GearQueryService gearQueryService;

    private final GearClassQueryService gearClassQueryService;

    @Override
    public Gear saveEntity(Gear entity) {
        return super.saveEntity(entity);
    }

    // 관리자만 사용가능하게 추후 수정
    @Override
    public void deleteById(String id) {
        super.deleteById(id);
    }

    @Transactional
    public void saveTempGear(TempGearRequest request) {
        Gear gear = Gear.builder()
                .id(null)
                .name(null) // name이 필드 제거 여부 결정 필요? 모델명 말고 다른걸 name이라고 부를만한 정보가 있는가?
                .modelName(request.getModelName())
                .brand(request.getBrand())
                .eqClass(gearClassQueryService.findByClassName(request.getGearClass()))
                .isTemp(true)
                .build();

        saveEntity(gear);
    }


    // 관리자만 사용가능하게 추후 수정
    @Transactional
    public void updateGear(TempGearRequest req, String gearId) {
        Gear targetGear = gearQueryService.findById(gearId);

        targetGear.updateTempGear(req, gearClassQueryService.findByClassName(req.getGearClass()));
        if(targetGear.isTemp()) targetGear.approveTempGear();

        saveEntity(targetGear);
    }

    // 관리자만 사용가능하게 추후 수정
    @Transactional
    public void approveTempGear(String tempGearId) {
        Gear targetTempGear = gearQueryService.findById(tempGearId);

        // tempGear가 아닌 경우
        if(!targetTempGear.isTemp()) throw new CustomException(ErrorCode.IS_NOT_TEMP_GEAR);

        targetTempGear.approveTempGear();

        saveEntity(targetTempGear);

    }

    @Override
    protected JpaRepository<Gear, String> getRepository() {
        return gearCommandRepository;
    }
}
