package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import com.notfound.lpickbackend.servicedata.command.domain.Album;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserAlbum;
import com.notfound.lpickbackend.servicedata.query.service.AlbumQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserAlbumCommandRepository;
import com.notfound.lpickbackend.userinfo.query.dto.response.FavoriteToggleStatus;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAlbumCommandService {
    private final S3Uploader s3Uploader; // 업로드 도중 문제 발생 시 삭제위해 여기서 사용

    private final UserInfoQueryService userInfoQueryService;
    private final AlbumQueryService albumQueryService;

    private final UserAlbumQueryService userAlbumQueryService;
    private final UserAlbumCommandRepository userAlbumCommandRepository;

    @Transactional
    public void applyUserAlbum(String userId, String albumId) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);
        Album album = albumQueryService.getAlbumById(albumId);

        UserAlbum userAlbum = UserAlbum.builder()
                .userAlbumId(null)
                .oauth(userInfo)
                .album(album)
                .recordFile(null)
                .isFavorite(false)
                .build();

        userAlbumCommandRepository.save(userAlbum);
    }

    public void deleteUserAlbum(String userAlbumId) {
        UserAlbum target = userAlbumQueryService.findById(userAlbumId);

        // s3 내 저장 파일 삭제.
        String uploadRecordURL = target.getRecordFile();
        s3Uploader.deleteFile(uploadRecordURL.split("/")[0], uploadRecordURL.split("/")[1]);

        userAlbumCommandRepository.deleteById(userAlbumId);
    }

    @Transactional
    public void patchAddUserAlbumRecordURL(String uploadedURL, String userAlbumId) {
        UserAlbum target = userAlbumQueryService.findById(userAlbumId);

        // 기존에 userAlbum에 대해 이미 업로드되어있던 record 파일이 있었다면 S3에서 삭제
        if(target.getRecordFile() != null) s3Uploader.deleteFile(
                target.getRecordFile().split("/")[0],
                target.getRecordFile().split("/")[1]
        );

        target.setRecordFile(uploadedURL);

        userAlbumCommandRepository.save(target);
    }
    @Transactional
    public void patchDelUserAlbumRecordURL(String userAlbumId) {
        UserAlbum target = userAlbumQueryService.findById(userAlbumId);

        // s3에서 삭제
        String uploadRecordURL = target.getRecordFile();
        s3Uploader.deleteFile(uploadRecordURL.split("/")[0], uploadRecordURL.split("/")[1]);

        // 매핑용 url 삭제
        target.setRecordFile(null);
    }


    @Transactional
    public FavoriteToggleStatus patchUserAlbumFavoriteToggle(String oAuthId, String userAlbumId) {
        

        UserAlbum target = userAlbumQueryService.findById(userAlbumId);
        boolean toggleStatus = !target.isFavorite();
        
        // favorite가 True인 앨범 개수가 10개 이상일때 + 현재 변경하고자 하는 toggleStatus가 True일 때(== 새 Favorite 추가 시도 시)
        if(userAlbumQueryService.countByisFavoriteTrue(oAuthId) >= 10 && toggleStatus)
            throw new CustomException(ErrorCode.ALREADY_FULL_FAVORITE_ALBUM);
        target.setFavorite(toggleStatus); // 토글 진행

        userAlbumCommandRepository.save(target);

        return new FavoriteToggleStatus(toggleStatus);
    }
}
