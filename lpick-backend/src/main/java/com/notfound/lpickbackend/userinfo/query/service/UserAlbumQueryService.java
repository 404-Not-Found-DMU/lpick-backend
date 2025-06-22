package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserAlbum;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import com.notfound.lpickbackend.userinfo.query.repository.UserAlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAlbumQueryService {

    private final UserAlbumQueryRepository userAlbumQueryReposiory;

    public Page<UserAlbumOwnedResponse> getUserAlbumListByUserId(String oAuthId, Pageable pageable) {
        Page<UserAlbum> userAlbumPage = userAlbumQueryReposiory.findAllByOauth_OauthId(oAuthId, pageable);

        return userAlbumPage.map(i -> {
            Album album = i.getAlbum();

            return UserAlbumOwnedResponse.builder(
                    .userAlbumId(i.getUserAlbumId())
                    .name(album.getName())
                    .profile(album.getProfile())
                    .artist(album.get) // albumArtist - artist 추출 들어가야함. jpa가 아닌 별도 서비스에서 도출 시도해야할 듯 보임
                    .recordFile(i.getRecordFile())
                    .releaseCountry(album.getReleaseCountry())
                    .releaseDate(album.getReleaseDate())
                    .label(album.getLabel())
                    .build();
        });
    }
}
