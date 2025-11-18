package com.notfound.lpickbackend.servicedata.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notfound.lpickbackend.common.elasticsearch.service.DataSyncService;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.servicedata.query.dto.AlbumSearchResultDTO;
import com.notfound.lpickbackend.servicedata.query.dto.GearSearchResultDTO;
import com.notfound.lpickbackend.servicedata.query.dto.ImageSearchResponse;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResult;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResultWithImage;
import com.notfound.lpickbackend.servicedata.query.service.AlbumQueryService;
import com.notfound.lpickbackend.servicedata.query.service.DiscogsApiService;
import com.notfound.lpickbackend.servicedata.query.service.UnifiedSearchService;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClassEnum;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/data")
@Tag(name = "검색 컨트롤러", description = "통합검색기능을 지니는 컨트롤러입니다.")
@Slf4j
public class SearchController {

    private final AlbumQueryService albumQueryService;
    private final DataSyncService dataSyncService; // 테스트/운영을 위한 동기화 엔드포인트
    private final UnifiedSearchService unifiedSearchService;
    private final DiscogsApiService discogsApiService;
    private final WikiPageQueryService wikiPageQueryService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 전체 앨범 데이터 동기화 (초기 셋업용)
     */
    @PostMapping("/sync")
    @Operation(summary = "DB <-> ElasticSearch 싱크 api", description = "db와 elasticsearch의 데이터를 맞추기 위한 api입니다.")
    public ResponseEntity<SuccessCode> syncAll() {

        // 김경환 수정
        // 기존의 sync 구현 방식은 인덱스 양식(mapping-json)을 참조하지 않고, 인덱스를 구현해야할 데이터를 불러온 뒤 각 타입별로 es가 '추론'하여 인덱스 구조를 구현중인 심각한 문제가있었습니다.
        // 이를 해결하기 위해 아래와 같이 수정합니다.

        // GPT 요약 첨부 :
        // mapping JSON = DB의 스키마(DDL, schema.sql)
        // Document 클래스 = ORM 엔티티
        // 현재 흐름은 “schema.sql도 안 돌리고, ORM도 안 쓰고, DB가 들어온 데이터 보고 컬럼 타입을 추정해서 테이블 만든” 상황과 같다.

        dataSyncService.recreateAndSyncAll();;

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

    @GetMapping("/autocomplete/gear")
    @Operation(summary = "장비 검색 자동완성 목록 api", description = "장비 검색어 자동완성 기능입니다. eqClass로 타입을 한정할 수 있습니다.")
    public ResponseEntity<List<GearSearchResultDTO>> autocompleteGear(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "eqClass", required = false)
            GearClassEnum eqClass,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        List<GearSearchResultDTO> results =
                unifiedSearchService.autocompleteGears(keyword, eqClass, size);
        return ResponseEntity.ok(results);
    }

    /**
     */
    @GetMapping("/search")
    @Operation(summary = "통합검색", description = "통합검색 기능입니다. 게시글과 위키를 포함합니다.")
    public ResponseEntity<List<SearchResult>> searchAllByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        return ResponseEntity.ok(unifiedSearchService.integratedSearch(keyword, pageable));
    }

    @GetMapping("/search/album")
    @Operation(summary = "앨범 검색", description = "앨범 검색 기능입니다.")
    public ResponseEntity<List<SearchResult>> searchAlbumsByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        return ResponseEntity.ok(unifiedSearchService.searchByType(keyword, pageable, new String[]{"albums"}));
    }

    @GetMapping("/search/gear")
    @Operation(summary = "장비 검색", description = "장비검색 기능입니다. eqClass로 타입을 한정할 수 있습니다.")
    public ResponseEntity<List<GearSearchResultDTO>> searchGearByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "eqClass", required = false)
            GearClassEnum eqClass, // TURNTABLE / SPEAKER / HEADPHONE 등
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<GearSearchResultDTO> results =
                unifiedSearchService.searchGears(keyword, pageable, eqClass);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/artist")
    @Operation(summary = "아티스트 검색", description = "아티스트 검색 기능입니다.")
    public ResponseEntity<List<SearchResult>> searchArtistByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        return ResponseEntity.ok(unifiedSearchService.searchByType(keyword, pageable, new String[]{"artists"}));
    }

    @GetMapping("/search/article")
    @Operation(summary = "게시글 검색", description = "게시글 검색 기능입니다.")
    public ResponseEntity<List<SearchResult>> searchArticleByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        // AlbumQueryService에서 구현한 통합 검색 메서드를 호출합니다.
        return ResponseEntity.ok(unifiedSearchService.searchByType(keyword, pageable, new String[]{"articles"}));
    }

    @GetMapping("/album/recommend")
    @Operation(summary = "앨범 추천", description = "LPTI기반으로 앨범을 추천합니다.")
    public ResponseEntity<List<AlbumSearchResultDTO>> getRecommendAlbums() {

        List<AlbumSearchResultDTO> results = albumQueryService.recommendRandomAlbums();

        for(AlbumSearchResultDTO result : results) {
            result.setImageUrl(discogsApiService.getPrimaryImageUrl(result.getAlbumId()));
        }

        return ResponseEntity.ok(results);
    }


    /**
     * 모든 인덱스 삭제 엔드포인트
     * 예) DELETE /admin/es/indices
     * 예) DELETE /admin/es/indices?includeSystem=true  -> 시스템 인덱스까지 다 삭제
     */
    @DeleteMapping("/indices")
    public ResponseEntity<Map<String, Object>> deleteAllIndices(
            @RequestParam(name = "includeSystem", defaultValue = "false") boolean includeSystem
    ) {
        List<String> deleted = unifiedSearchService.deleteAllIndices(includeSystem);

        Map<String, Object> body = new HashMap<>();
        body.put("deleted", deleted);
        body.put("count", deleted.size());

        return ResponseEntity.ok(body);
    }

    @PostMapping(
            value = "/search/album/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "ai 이미지 검색", description = "이미지를 업로드하여 앨범을 검색합니다.")
    public ResponseEntity<List<SearchResultWithImage>> searchByImage(
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        try {
            // 1) FastAPI URL
            String url = "https://ai.lpick.in/search/image?top_k=10";

            // 2) multipart/form-data 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 3) MultipartFile → ByteArrayResource
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // 4) multipart 바디 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));

            HttpEntity<ByteArrayResource> filePart =
                    new HttpEntity<>(fileResource, fileHeaders);

            body.add("file", filePart); // FastAPI의 UploadFile 이름은 반드시 "file"

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // 5) FastAPI 호출
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, requestEntity, String.class);

            // 6) JSON → DTO 변환
            ImageSearchResponse result =
                    objectMapper.readValue(response.getBody(), ImageSearchResponse.class);

            // 7) 결과
            List<SearchResultWithImage> responseList = wikiPageQueryService.findByImage(result);

            // 8) 결과에 이미지 삽입
            for(SearchResultWithImage r : responseList) {
                r.setImageUrl(discogsApiService.getPrimaryImageUrl(r.getAlbumId()));
            }

            responseList.sort( // Similarity 순 정렬
                    Comparator.comparingDouble(SearchResultWithImage::getSimilarity).reversed()
            );

            // 눈물
            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("이미지 검색 호출 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/discogs/test")
    public ResponseEntity<String> testDiscogsApi(
            @RequestParam String id
    ) {
        String imageUrl = discogsApiService.getPrimaryImageUrl(id);

        log.warn(imageUrl);

        return ResponseEntity.ok(imageUrl);
    }
}
