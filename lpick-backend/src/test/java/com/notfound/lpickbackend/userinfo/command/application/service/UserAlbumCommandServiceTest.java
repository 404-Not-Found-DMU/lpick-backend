package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserAlbum;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAlbumCommandServiceTest {
    @Mock
    private UserAlbumQueryService userAlbumQueryService;

    @InjectMocks
    private UserAlbumCommandService userAlbumCommandService;


    private UserAlbum mockUserAlbum;

    private final String oauthId = "oauth123";
    private final String userAlbumId = "1";

    private final String albumId = "album123";

    @BeforeEach
    void setUp() {
            UserInfo mockUser = UserInfo.builder()
                    .oauthId(oauthId) // 전치사로 OAuthType 추가
                    .nickname("")
                    .profile("")
                    .point(0)
                    .stackPoint(0)
                    .about("")
                    .lpti("")
                    .tier(null)
                    .build();

            Album mockAlbum = Album.builder()
                    .albumId(albumId)
                    .name("")
                    .profile("")
                    .label("")
                    .releaseCountry("")
                    .releaseDate(Instant.now())
                    .wiki(null)
                    .build();


            mockUserAlbum = UserAlbum.builder()
                    .userAlbumId(userAlbumId)
                    .isFavorite(false)
                    .recordFile(null)
                    .oauth(mockUser)
                    .album(mockAlbum)
                    .build();
        }

        @Test
        void toggleFavoriteIfFavoriteCountUpper10() {


            given(userAlbumQueryService.findById(userAlbumId)).willReturn(mockUserAlbum);
            given(userAlbumQueryService.countByisFavoriteTrue(oauthId)).willReturn(11L);

            CustomException exception = assertThrows(CustomException.class, () -> {
                userAlbumCommandService.patchUserAlbumFavoriteToggle(oauthId, userAlbumId);
            });


            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FULL_FAVORITE_ALBUM);

        }
}