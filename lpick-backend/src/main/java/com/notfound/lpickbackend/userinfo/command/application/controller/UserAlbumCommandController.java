package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.dto.request.UserAlbumApplyRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserAlbumCommandService;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserAlbumCommandController {
    private final UserInfoQueryService userInfoQueryService;

    private final UserAlbumCommandService userAlbumCommandService;

    /**
     * DB에 이미 존재하는 Album의 id를 requestbody로 받아 user-album에 등록한다.
     *
     * @param req Album 엔티티의 id를 기입받음.
     *
     * */
    @PostMapping("/user-album")
    public ResponseEntity<SuccessCode> applyUserAlbum(
            @RequestBody @Valid UserAlbumApplyRequest req
    ) {
        userAlbumCommandService.applyUserAlbum(UserInfoUtil.getOAuthId(), req.getAlbumId());

        return ResponseEntity.ok(SuccessCode.USER_ALBUM_CREATE_SUCCESS);
    }

    /**
     * 사용자가 자신의 레코드 파일 기입
     * S3에 녹음 파일을 업로드.
     * */
    @PatchMapping("/user-album/{userAlbumId}/record")
    public ResponseEntity<SuccessCode> applyRecordFile(
            @PathVariable("userAlbumId") String userAlbumId,
            @RequestParam("audioFile") MultipartFile audioFile
    ) {

    }

    @DeleteMapping("/user-album/{userAlbumId}")
    public ResponseEntity<SuccessCode> deleteUserAlbum(
            @PathVariable("userAlbumId") String userAlbumId
    ) {
        userAlbumCommandService.deleteUserAlbum(userAlbumId);

        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }


}
