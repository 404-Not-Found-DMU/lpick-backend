package com.notfound.lpickbackend.servicedata.command.application.controller;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.dto.TempGearRequest;
import com.notfound.lpickbackend.servicedata.command.application.service.GearCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "음향기기 컨트롤러", description = "'임시' 음향기기 추가/수정/삭제 가능.")

public class GearCommandController {

    private final GearCommandService gearCommandService;

    @PostMapping("/gear/temp-gear")
    @Operation(summary = "임시 음향기기 추가", description = "사용자가 자신의 음향기기가 본 서비스의 DB에 없어 등록하지 못할경우 사용.")
    public ResponseEntity<SuccessCode> createTempGear(
            @RequestBody @Valid TempGearRequest req,
            @RequestPart("image")MultipartFile images
            ) {
        gearCommandService.saveTempGear(req);

        return ResponseEntity.ok(SuccessCode.CREATE_SUCCESS);
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
