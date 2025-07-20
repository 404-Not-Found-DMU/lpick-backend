package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.UserGearCollectionResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserGearQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserGearQueryController {
    private final UserGearQueryService userGearQueryService;

    @GetMapping("/user/gear")
    public ResponseEntity<UserGearCollectionResponse> getUserOwnedGearList() {
        return ResponseEntity.ok().body(userGearQueryService.getUserOwnedGear(UserInfoUtil.getOAuthId()));
    }

    @GetMapping("/my-page/{oauthId}/gear")
    public ResponseEntity<BlindableResponse<UserGearCollectionResponse>> getUserOwnedGearList(
            @PathVariable("oauthId") String oauthId
    ) {

        return ResponseEntity.ok().body(userGearQueryService.getUserOwnedGearByUserId(oauthId));
    }
}
