package com.notfound.lpickbackend.servicedata.command.application.service;

import com.notfound.lpickbackend.common._super.BaseCommandService;
import com.notfound.lpickbackend.common.dto.IdResponse;
import com.notfound.lpickbackend.common.elasticsearch.service.DataSyncService;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.json_schema_draft.GearSpecSchema;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.command.application.domain.dto.TempGearRequest;
import com.notfound.lpickbackend.servicedata.command.application.repository.GearCommandRepository;
import com.notfound.lpickbackend.servicedata.query.service.GearClassQueryService;
import com.notfound.lpickbackend.servicedata.query.service.GearQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GearCommandService {

    private final GearCommandRepository gearCommandRepository;

    private final GearQueryService gearQueryService;

    private final GearClassQueryService gearClassQueryService;

    private final S3Uploader s3Uploader;

    private final GearSpecSchema gearSpecSchema;

    private final DataSyncService dataSyncService;


    public Gear saveEntity(Gear entity) {
        return gearCommandRepository.save(entity);
    }

    // 관리자만 사용가능하게 추후 수정
    public void deleteById(String id) {
        gearCommandRepository.deleteById(id);
    }

    @Transactional
    public IdResponse saveTempGear(TempGearRequest request, MultipartFile img) {
        
        /* 제공된 스키마 검증 */
//        gearSpecSchema.validateSpecsOrThrow(request.getSpecJson(), request.getGearClass());
        // 스키마 문제 발견되어 주석처리. 미사용 예정
        
        /* 이미지 등록 */
        String imgDir = null;

        if (img != null && !img.isEmpty()) {
            String value = request.getGearClass();  // 예: TURNTABLE

            String gearImgDir = new StringBuilder("Gear/")
                    .append(value)
                    .toString();

            try {
                imgDir = s3Uploader.upload(img, gearImgDir);
            } catch(Exception e) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        Gear gear = this.saveEntity(
                Gear.builder()
                    .gearId(null)
                    .name(new StringBuilder((request.getBrand()))
                            .append(" ")
                            .append(request.getModelName())
                            .toString()
                    ) // 브랜드
                    .modelName(request.getModelName())
                    .brand(request.getBrand())
                    .eqClass(gearClassQueryService.findByClassName(request.getGearClass()))
                    .isTemp(true)
                    .img(imgDir)
                    .meta(request.getSpecJson())
                    .build()
                );

        dataSyncService.syncGear(gear);


        return new IdResponse(
                gear.getGearId()
        );
    }


    // 관리자만 사용가능하게 추후 수정
    @Transactional
    public void updateGear(TempGearRequest req, String gearId) {
        Gear targetGear = this.findGearById(gearId);

        targetGear.updateTempGear(req, gearClassQueryService.findByClassName(req.getGearClass()));
        if(targetGear.isTemp()) targetGear.approveTempGear();

        this.saveEntity(targetGear);
    }

    // 관리자만 사용가능하게 추후 수정
    @Transactional
    public void approveTempGear(String tempGearId) {
        Gear targetTempGear = this.findGearById(tempGearId);

        // tempGear가 아닌 경우
        if(!targetTempGear.isTemp()) throw new CustomException(ErrorCode.IS_NOT_TEMP_GEAR);

        targetTempGear.approveTempGear();

        this.saveEntity(targetTempGear);

    }

    private Gear findGearById(String gearId) {
        return gearCommandRepository.findById(gearId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GEAR));
    }
}
