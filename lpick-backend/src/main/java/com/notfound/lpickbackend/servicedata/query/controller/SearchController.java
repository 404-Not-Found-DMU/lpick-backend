package com.notfound.lpickbackend.servicedata.query.controller;

import com.notfound.lpickbackend.common.elasticsearch.service.DataSyncService;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.servicedata.query.dto.AlbumSearchResultDTO;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResult;
import com.notfound.lpickbackend.servicedata.query.service.AlbumQueryService;
import com.notfound.lpickbackend.servicedata.query.service.UnifiedSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/data")
@Tag(name = "검색 컨트롤러", description = "통합검색기능을 지니는 컨트롤러입니다.")
@Slf4j
public class SearchController {

    private final AlbumQueryService albumQueryService;
    private final DataSyncService dataSyncService; // 테스트/운영을 위한 동기화 엔드포인트
    private final UnifiedSearchService unifiedSearchService;

    /**
     * 전체 앨범 데이터 동기화 (초기 셋업용)
     */
    @PostMapping("/sync")
    @Operation(summary = "DB <-> ElasticSearch 싱크 api", description = "db와 elasticsearch의 데이터를 맞추기 위한 api입니다.")
    public ResponseEntity<SuccessCode> syncAll() {
        dataSyncService.syncAllAlbums();
        dataSyncService.syncAllArtists();
        dataSyncService.syncAllGears();
        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @GetMapping("/autocomplete")
    @Operation(summary = "검색어 자동 완성 목록 api", description = "검색어를 입력할 때 마다 자동완성된 검색어 목록을 제공합니다.")
    public ResponseEntity<List<SearchResult>> getAutocompleteSuggestions(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "size", defaultValue = "10") int size // 반환 개수 지정
    ) {
        List<SearchResult> suggestions = unifiedSearchService.autocompleteSuggestions(keyword, size);
        log.warn("suggestions: {}", suggestions);
        return ResponseEntity.ok(suggestions);
    }

    /**
     */
    @GetMapping("/search")
    @Operation
    public ResponseEntity<List<SearchResult>> searchAlbumsByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        return ResponseEntity.ok(unifiedSearchService.integratedSearch(keyword, pageable));
    }
}
