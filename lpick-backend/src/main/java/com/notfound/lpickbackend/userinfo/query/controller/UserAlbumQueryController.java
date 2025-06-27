package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import com.notfound.lpickbackend.userinfo.query.service.UserAlbumQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 소유 앨범 조회 컨트롤러", description = "사용자가 소유한 앨범 리스트, 상세정보, favorite 리스트, 장르별 카운트 개수 제공")
public class UserAlbumQueryController {
        private final UserAlbumQueryService userAlbumQueryService;

        @GetMapping("/user-album")
        @Operation(summary = "사용자 소유 앨범 페이지네이션 조회", description = "페이지네이션을 기반으로 사용자가 소유한 앨범의 목록 조회 가능. 상세 조회를 바로 제공.(필요시 제목 및 커버만 반환으로 수정가능)")
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
        @Operation(summary = "사용자가 소유한 단일 앨범 상세조회", description = "'사용자 소유 앨범(UserAlbum)' id를 기반으로 상세조회 가능")
        public ResponseEntity<UserAlbumOwnedResponse> getUserOwnedAlbumInfo(
                @PathVariable("userAlbumId") String userAlbumId
        ) {
                return ResponseEntity.status(HttpStatus.OK).body(userAlbumQueryService.getUserAlbumById(userAlbumId));
        }


        /** 사용자가 설정한 favorite 리스트 제공.*/
        @GetMapping("/user-album/favorite")
        @Operation(summary = "사용자가 소유한 favorite 앨범 목록 조회", description = "사용자가 추천 앨범으로 선정해둔 앨범의 목록만 조회.(최대 10개)")
        public ResponseEntity<List<UserAlbumOwnedResponse>> getUserOwnedFavoriteAlbum(

        ) {
                return ResponseEntity.status(HttpStatus.OK).body(userAlbumQueryService.getUserFavoriteAlbumList(UserInfoUtil.getOAuthId()));
        }

        /** 각 장르별 앨범 개수 집계하여 제공. 다른 경우에도 활용될 가능성 있어 일단 requestParam으로 설정. */
        @GetMapping("/user-album/count")
        @Operation(summary = "사용자가 소유한 앨범의 장르별 count 제공", description = "사용자의 소유 앨범을 장르별로 집계하여 각 장르별 카운트 Map을 제공. 장르 중복 가능(어느 한 앨범이 jazz, pop 인경우 각각에 +1) ")
        public ResponseEntity<Map<String, Integer>> getUserOwnedAlbumCountByGenre(
        ) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(userAlbumQueryService.countUserAlbumByGenre(UserInfoUtil.getOAuthId()));
        }


}
