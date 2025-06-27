package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.service.GenreQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserAlbum;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import com.notfound.lpickbackend.userinfo.query.repository.UserAlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAlbumQueryService {

    private final UserAlbumQueryRepository userAlbumQueryReposiory;
    private final GenreQueryService genreQueryService;

    public Page<UserAlbumOwnedResponse> getUserAlbumListByUserId(String oAuthId, Pageable pageable) {

        return userAlbumQueryReposiory.findAllUserAlbumByOauthId(oAuthId, pageable);
    }

    public UserAlbumOwnedResponse getUserAlbumById(String userAlbumId) {
        Optional<UserAlbumOwnedResponse> userAlbumOptional = userAlbumQueryReposiory.findUserAlbumById(userAlbumId);

        if(userAlbumOptional.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND_USER_ALBUM);

        return userAlbumOptional.get();
    }

    public List<UserAlbumOwnedResponse> getUserFavoriteAlbumList(String oAuthId) {
        return userAlbumQueryReposiory.findAllUserAlbumByIsFavorite(oAuthId);
    }

    public Map<String, Integer> countUserAlbumByGenre(String oAuthId) {
        // 1. 현재 등록된 전체 장르 목록 가져오기
        List<String> genreStrList = genreQueryService.getAllGenreList();

        if(genreStrList.size() == 0) return new HashMap<>(); // 비어있는 해시맵 반환

        // 2. 가져온 전체 장르 목록에 따른 장르별 카운트 개수 가져오기
        return userAlbumQueryReposiory.countUserAlbumByGenre(oAuthId).stream()
                .collect(Collectors.toMap(
                        row  -> (String) row[0],
                        row  -> ((Long)  row[1]).intValue(),
                        (a,b)->a,
                        LinkedHashMap::new     // JPQL ORDER BY 순서 보존
                ));

    }

    public UserAlbum findById(String userAlbumId) {
        Optional<UserAlbum> userAlbumOptional = userAlbumQueryReposiory.findById(userAlbumId);
        if(userAlbumOptional.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER_ALBUM);
        }

        return userAlbumOptional.get();
    }

    public long countByisFavoriteTrue(String oAuthId) {
        return userAlbumQueryReposiory.countByOauth_OauthIdAndIsFavoriteTrue(oAuthId);
    }
}
