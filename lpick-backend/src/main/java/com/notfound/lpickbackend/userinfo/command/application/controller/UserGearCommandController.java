package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserGearPostRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserGearCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "사용자 음향기기 관련 CUD 컨트롤러", description = "사용자 음향기기 추가/삭제")
public class UserGearCommandController {
    private final UserGearCommandService userGearCommandService;

    @PostMapping("/user/gear")
    @Operation(summary = "사용자 음향기기 추가", description = "사용자의 음향기기를 추가. 현재 기준, Gear 테이블 미존재하는 기기 추가는 불가능.")
    public void createNewUserGear(
            @RequestBody @Valid UserGearPostRequest userGearPostRequest
    ) {
        userGearCommandService.createNewUserGear(UserInfoUtil.getOAuthId(), userGearPostRequest);
    }

//    @PatchMapping("/user/gear")
//    public void patchUserGear() {
//
//    }


    @DeleteMapping("/user/gear/{userGearId}")
    @Operation(summary = "사용자 음향기기 제거", description = "사용자의 음향기기를 제거.")
    public void deleteUserGear(
            @PathVariable("userGearId") String userGearId
    ) {
        userGearCommandService.deleteUserGear(UserInfoUtil.getOAuthId(), userGearId);
    }
}
