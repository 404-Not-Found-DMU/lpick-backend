package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserGearPostRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserGearCommandService;
import com.notfound.lpickbackend.userinfo.query.dto.response.FavoriteToggleStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/user/gear/{userGearId}/favorite-toggle")
    @Operation(summary = "사용자 소유 음향기기에 대한 favorite 토글", description = "사용자가 자신이 소유한 음향기기에 대해 favorite 토글을 진행하여 favorite 리스트에 추가가능. favorite 리스트는 마이페이지에서 별도 표기됨. 요청 시 마다 업데이트 결과 boolean 값을 전달해주므로, 해당 값을 기반으로 프론트측 업데이트 진행.")
    public ResponseEntity<FavoriteToggleStatus> toggleUserGearFavorite(
            @PathVariable("userGearId") String userGearId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userGearCommandService.patchUserGearFavoriteToggle(
                        UserInfoUtil.getOAuthId(),
                        userGearId
                )
        );
    }

    @DeleteMapping("/user/gear/{userGearId}")
    @Operation(summary = "사용자 음향기기 제거", description = "사용자의 음향기기를 제거.")
    public void deleteUserGear(
            @PathVariable("userGearId") String userGearId
    ) {
        userGearCommandService.deleteUserGear(UserInfoUtil.getOAuthId(), userGearId);
    }
}
