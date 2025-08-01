package com.notfound.lpickbackend.servicedata.command.application.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// create, update 모두 사용
@Getter
@AllArgsConstructor
@Builder
public class TempGearRequest {
    @NotEmpty
    private String modelName;

    @NotEmpty
    @Pattern(
            regexp = "SPEAKER|HEADPHONE|TURNTABLE",
            message = "gearCategory는 SPEAKER, HEADPHONE, TURNTABLE 중 하나여야 합니다."
    )
    private String gearClass;

    private String brand; // 브랜드는 미상일 수 있음
}
