package com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGearPostRequest {
    @NotEmpty
    private String GearId;

    @NotEmpty
    @Pattern(
            regexp = "SPEAKER|HEADPHONE|TURNTABLE",
            message = "gearCategory는 SPEAKER, HEADPHONE, TURNTABLE 중 하나여야 합니다."
    )
    private String gearClass;
}
