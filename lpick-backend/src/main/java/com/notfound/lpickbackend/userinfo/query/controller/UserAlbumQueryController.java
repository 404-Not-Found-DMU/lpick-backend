package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserAlbumQueryController {
        private final UserAlbumQueryService userAlbumQueryService;

        @GetMapping("/user-album")
        public ResponseEntity<Page<UserAlbumOwnedResponse>> getUserOwnedAlbumList(
                @RequestParam("page")int page,
                @RequestParam("size")int size
        ) {
                Page<UserAlbumOwnedResponse> userAlbumList
                        = userAlbumQueryService.getUserAlbumListByUserId(
                                UserInfoUtil.getOAuthId(),
                                PageRequest.of(page, size)
                        );

                return ResponseEntity.status(HttpStatus.OK).body(userAlbumList);
        }

        @GetMapping("/user-album/{{userAlbumId}}")
        public ResponseEntity<UserAlbumOwnedResponse> getUserOwnedAlbumInfo(
                @PathVariable("userAlbumId") String userAlbumId
        ) {
                return ResponseEntity.status(HttpStatus.OK).body(userAlbumQueryService.getUserAlbumById(userAlbumId));
        }


        /** 사용자가 설정한 favorite 리스트 제공.*/
        @GetMapping("/user-album/favorite")
        public ResponseEntity<List<UserAlbumOwnedResponse>> getUserOwnedFavoriteAlbum(

        ) {
                return ResponseEntity.status(HttpStatus.OK).body(userAlbumQueryService.getUserFavoriteAlbumList(UserInfoUtil.getOAuthId()));
        }

        /** 각 장르별 앨범 개수 집계하여 제공. 다른 경우에도 활용될 가능성 있어 일단 requestParam으로 설정. */
        @GetMapping("/user-album/count")
        public ResponseEntity<Map<String, Integer>> getUserOwnedAlbumCountByGenre(
        ) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(userAlbumQueryService.countUserAlbumByGenre(UserInfoUtil.getOAuthId()));
        }


}
