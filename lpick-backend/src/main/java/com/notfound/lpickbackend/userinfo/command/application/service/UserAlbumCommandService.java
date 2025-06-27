package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.servicedata.command.domain.Album;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserAlbum;
import com.notfound.lpickbackend.servicedata.query.service.AlbumQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserAlbumCommandRepository;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAlbumCommandService {
    private final UserInfoQueryService userInfoQueryService;
    private final AlbumQueryService albumQueryService;

    private final UserAlbumQueryService userAlbumQueryService;
    private final UserAlbumCommandRepository userAlbumCommandRepository;
    public void applyUserAlbum(String userId, String albumId) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);
        Album album = albumQueryService.getAlbumById(albumId);

        UserAlbum userAlbum = UserAlbum.builder()
                .userAlbumId(null)
                .oauth(userInfo)
                .album(album)
                .recordFile(null)
                .build();

        userAlbumCommandRepository.save(userAlbum);
    }

    public void deleteUserAlbum(String userAlbumId) {
        userAlbumCommandRepository.deleteById(userAlbumId);
    }
}
