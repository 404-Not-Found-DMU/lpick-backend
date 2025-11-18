package com.notfound.lpickbackend.servicedata.command.application.controller;

import com.notfound.lpickbackend.common.dto.IdResponse;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.dto.TempGearRequest;
import com.notfound.lpickbackend.servicedata.command.application.service.GearCommandService;
import com.notfound.lpickbackend.servicedata.command.application.service.GearDefaultSettingService;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClassEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "음향기기 컨트롤러", description = "'임시' 음향기기 추가/수정/삭제 가능.")

public class GearCommandController {

    private final GearCommandService gearCommandService;
    private final GearDefaultSettingService gearDefaultSettingService;

    /** 전처리된 FLAT CSV 업로드 (meta.specs = 표시 라벨 → 값) */
    @PostMapping(value = "/public/flat-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "!!DB 기입용!!", description = "[개발자] 지정된 csv 받아 로컬/배포 db에 gear 초기데이터 기입 목적. 클라이언트 연동대상 XXX")
    public ResponseEntity<Map<String, Object>> importFlatCsv(
            @RequestPart("file") MultipartFile csv,
            @RequestParam(defaultValue = "false") boolean dryRun,
            @RequestParam GearClassEnum gearClass
            ) throws Exception {
        var r = gearDefaultSettingService.importFlatCsv(csv, dryRun, gearClass);
        return ResponseEntity.ok(Map.of(
                "inserted", r.inserted(),
                "updated",  r.updated(),
                "skipped",  r.skipped(),
                "total",    r.total(),
                "dryRun",   r.dryRun()
        ));
    }

    @PostMapping(
            value = "/gear/temp-gear",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "임시 음향기기 추가", description = "[사용자] 사용자가 자신의 음향기기가 본 서비스의 DB에 없어 등록하지 못할경우 사용.")
    public ResponseEntity<IdResponse> createTempGear(
            @RequestPart @Valid TempGearRequest req,
            @RequestPart(value = "image", required = false)MultipartFile image
            ) {

        return ResponseEntity.ok(gearCommandService.saveTempGear(req,image));
    }

    @PatchMapping("/gear/{gear-id}/update-info")
    @Operation(summary = "음향기기 정보 수정 및 승인", description = "[관리자] 잘못 설정된 음향기기의 정보 수정 목적. 임시 음향기기를 수정할 경우 approve 한 것으로 간주.")
    public ResponseEntity<SuccessCode> updateGearInfo(
            @PathVariable("gear-id") String tempGearId,
            @RequestBody @Valid TempGearRequest req ) {

        gearCommandService.updateGear(req, tempGearId);

        return ResponseEntity.ok(SuccessCode.GEAR_UPDATE_SUCESS);
    }


    // 추후 관리자만 사용 가능한 기능으로 설정하기
    @PatchMapping("/gear/temp-gear/{temp-gear-id}/approve")
    @Operation(summary = "임시 음향기기 승인", description = "[관리자] 사용자가 임시로 설정해둔 음향기기를 승인.")
    public ResponseEntity<SuccessCode> approveTempGear(
            @PathVariable("temp-gear-id") String tempGearId
    ) {
        gearCommandService.approveTempGear(tempGearId);

        return ResponseEntity.ok(SuccessCode.TEMP_GEAR_APPROVE_SUCESS);
    }

    // 추후 관리자만 사용 가능한 기능으로 설정하기
    @DeleteMapping("/gear/{gear-id}")
    @Operation(summary = "음향기기 삭제", description = "[관리자] 음향기기를 DB에서 삭제. 잘못 승인된 음향기기 등 제거 목적")
    public ResponseEntity<SuccessCode> deleteTempGear(
            @PathVariable("gear-id") String tempGearId
    ) {
        gearCommandService.deleteById(tempGearId);

        return ResponseEntity.ok(SuccessCode.TEMP_GEAR_APPROVE_SUCESS);
    }
}
