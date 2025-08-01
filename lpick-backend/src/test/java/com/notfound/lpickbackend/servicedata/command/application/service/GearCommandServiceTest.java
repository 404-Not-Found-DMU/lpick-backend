package com.notfound.lpickbackend.servicedata.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.command.application.repository.GearCommandRepository;
import com.notfound.lpickbackend.servicedata.query.service.GearClassQueryService;
import com.notfound.lpickbackend.servicedata.query.service.GearQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GearCommandServiceTest {

    @Mock
    private GearCommandRepository gearCommandRepository;

    // baseService, baseEntity 리팩토링된 대상 서비스에는 @Spy 적용해야 테스트 추적 정상작동.
    @Spy
    @InjectMocks
    private GearCommandService gearCommandService;

    @Mock
    private GearQueryService gearQueryService;

    @Mock
    private GearClassQueryService gearClassQueryService;

    private final String GEAR_ID = "gear-1";

    private Gear gear;

    @BeforeEach
    void setUp() {
        gear = Gear.builder()
                .id(GEAR_ID)
                .isTemp(false)
                .build();
    }

    @Test
    @DisplayName("approveTempGearIfNotTempGear : tempGear가 아닌 Gear에 대해 approve를 시도하는 경우의 예외처리를 검증한다.")
    void approveTempGearIfNotTempGear() {

        given(gearQueryService.findById(GEAR_ID)).willReturn(gear);

        CustomException exception = assertThrows(CustomException.class, () -> {
            gearCommandService.approveTempGear(GEAR_ID);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.IS_NOT_TEMP_GEAR);
    }


}