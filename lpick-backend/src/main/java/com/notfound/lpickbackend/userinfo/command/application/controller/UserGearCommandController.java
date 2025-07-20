package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserGearPostRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserGearCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserGearCommandController {
    private final UserGearCommandService userGearCommandService;

    @PostMapping("/user/gear")
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
    public void deleteUserGear(
            @PathVariable("userGearId") String userGearId
    ) {
        userGearCommandService.deleteUserGear(UserInfoUtil.getOAuthId(), userGearId);
    }
}
