package com.notfound.lpickbackend.servicedata.query.controller;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.service.AlbumSyncService;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.servicedata.query.service.AlbumQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/album")
@Slf4j
public class AlbumQueryController {

    private final AlbumQueryService albumQueryService;
    private final AlbumSyncService albumSyncService; // 테스트/운영을 위한 동기화 엔드포인트

    /**
     * 전체 앨범 데이터 동기화 (초기 셋업용)
     */
    @PostMapping("/sync")
    public ResponseEntity<SuccessCode> syncAllAlbums() {
        albumSyncService.syncAllAlbums();
        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions(
            @RequestParam("q") String query,
            @RequestParam(value = "size", defaultValue = "10") int size // 반환 개수 지정
    ) {
        List<String> suggestions = albumQueryService.searchAutocompleteSuggestions(query, size);
        return ResponseEntity.ok(suggestions);
    }

    /**
     */
    @GetMapping("/search")
    public ResponseEntity<List<AlbumDocument>> searchAlbumsByKeyword(@RequestParam("keyword") String keyword) {
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        List<AlbumDocument> searchResults = albumQueryService.searchAlbumsByKeyword(keyword);
        return ResponseEntity.ok(searchResults);
    }
}
