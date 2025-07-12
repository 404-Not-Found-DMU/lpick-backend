package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserAlbumApplyRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserAlbumCommandService;
import com.notfound.lpickbackend.userinfo.query.dto.response.FavoriteToggleStatus;
import com.notfound.lpickbackend.userinfo.query.repository.UserAlbumQueryRepository;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자 소유 앨범 CUD 컨트롤러", description = "사용자가 소유한 앨범 추가, 삭제, 업데이트 등을 담당. S3 기반 녹음 파일 추가,삭제 가능")
public class UserAlbumCommandController {
    private final UserAlbumQueryRepository userAlbumQueryRepository;
    private final S3Uploader s3Uploader; // S3에 멀티파트 업로드를 위한 구현 클래스

    private final UserAlbumCommandService userAlbumCommandService;
    private final UserAlbumQueryService userAlbumQueryService;

    /**
     * DB에 이미 존재하는 Album의 id를 requestbody로 받아 user-album에 등록한다.
     *
     * @param req Album 엔티티의 id를 기입받음.
     * */
    @PostMapping("/user-album")
    @Operation(summary = "사용자 소유 앨범 추가", description = "현재 서비스에서 저장중인 앨범 목록을 기반으로 사용자 소유의 앨범 추가(현재로서는 서비스 내 앨범 목록에 없으면 추가 불가능)")
    public ResponseEntity<SuccessCode> applyUserAlbum(
            @RequestBody @Valid UserAlbumApplyRequest req
    ) {
        userAlbumCommandService.applyUserAlbum(UserInfoUtil.getOAuthId(), req.getAlbumId());

        return ResponseEntity.ok(SuccessCode.USER_ALBUM_CREATE_SUCCESS);
    }

    /** favorite 토글을 연속해서 보낼 경우 요청과 실제 DB 업데이트가 완료되기 전에 다음 요청이 간다거나.. 할 수 있음.
     * 이로 인해 front와 back의 실제 favorite 상태 다른 경우(== 페이지의 하트 모양과 DB 내 상태) 방지 위해 favorite-toggle 요청 완료시마다 현재 상태를 반환해준다. */
    @PatchMapping("/user-album/{userAlbumId}/favorite-toggle") // 요청 ContentType이 MediaType.MULTIPART_FORM_DATA_VALUE일때만 본 요청에 매칭됨.
    @Operation(summary = "사용자 소유 앨범에 대한 favorite 토글", description = "사용자가 자신이 소유한 앨범에 대해 favorite 토글을 진행하여 favorite 리스트에 추가가능. favorite 리스트는 마이페이지에서 별도 표기됨. favorite는 10개 이상일 수 없음. 요청 시 마다 업데이트 결과 boolean 값을 전달해주므로, 해당 값을 기반으로 프론트측 업데이트 진행.")
    public ResponseEntity<FavoriteToggleStatus> applyRecordFile(
            @PathVariable("userAlbumId") String userAlbumId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userAlbumCommandService.patchUserAlbumFavoriteToggle(
                UserInfoUtil.getOAuthId(),
                userAlbumId
                )
        );
    }

    /**
     * 사용자가 자신의 레코드 파일 기입
     * S3에 녹음 파일을 업로드하는 목적이지만, 이미 사용자가 지니고있는 앨범에 대해 patch로 올리게끔 유도하였음.
     * 필요시 추후 POST에도 동일 로직 추가하여 사용자가 지닌 앨범 등록시 곧바로 기입 가능하도록 수정.
     * */
    @PatchMapping(path = "/user-album/{userAlbumId}/record",
            consumes  = MediaType.MULTIPART_FORM_DATA_VALUE) // 요청 ContentType이 MediaType.MULTIPART_FORM_DATA_VALUE일때만 본 요청에 매칭됨.
    @Operation(summary = "사용자 소유 앨범에 대한 녹음 파일 추가", description = "사용자가 자신이 소유한 앨범에 대해 녹음 파일 추가 가능. 최대 용량 300MB, record 디렉토리 내부에, UUID + originalName 이용해 저장됨.")
    public ResponseEntity<SuccessCode> applyRecordFile(
            @PathVariable("userAlbumId") String userAlbumId,
            @RequestParam("audioFile") MultipartFile audioFile
    ) throws IOException {
        // 0) userAlbumId의 존재여부 확인하여, 현재 존재하지 않는 ID에 대해 요청한 경우 에러 전달
        userAlbumQueryService.isExsistsUserAlbum(userAlbumId);

        // 1) S3에 먼저 업로드
        String uploadedURL = s3Uploader.upload(audioFile, "record");

        // uploadURL을 userAlbum의 record 필드에 저장하기.
        try {
            // 2) DB 업데이트
            userAlbumCommandService.patchAddUserAlbumRecordURL(uploadedURL, userAlbumId);
        } catch (Exception ex) {
            // 3) DB 업데이트 실패 시 보상 로직: 방금 올린 파일 삭제
            s3Uploader.deleteByUrl(uploadedURL);
            throw ex;  // 예외를 그대로 던지거나, 적절한 에러 응답 생성
        }
        return ResponseEntity.ok(SuccessCode.RECORD_CREATE_SUCCESS);
    }


    /**
     * 사용자가 자신의 레코드 파일을 삭제.
     * 1. S3 업로드된 파일 삭제
     * 2. UserAlbum의 record 필드 내역을 제거.
     * */
    @DeleteMapping("/user-album/{userAlbumId}/record")
    @Operation(summary = "사용자 소유 앨범에 대한 녹음 파일 제거", description = "S3내 실제 파일 및 테이블 내 url 경로 값 제거.")
    public ResponseEntity<SuccessCode> deleteRecordFile(
            @PathVariable("userAlbumId") String userAlbumId
    ) {
        userAlbumQueryService.isExsistsUserAlbum(userAlbumId);

        userAlbumCommandService.patchDelUserAlbumRecordURL(userAlbumId);

        return ResponseEntity.ok(SuccessCode.USER_ALBUM_RECORD_DELETE_SUCCESS);
    }

    @DeleteMapping("/user-album/{userAlbumId}")
    @Operation(summary = "사용자 소유 앨범 제거", description = "사용자가 현재 소유한 앨범을 제거함.")
    public ResponseEntity<SuccessCode> deleteUserAlbum(
            @PathVariable("userAlbumId") String userAlbumId
    ) {
        userAlbumCommandService.deleteUserAlbum(userAlbumId);

        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }


}
