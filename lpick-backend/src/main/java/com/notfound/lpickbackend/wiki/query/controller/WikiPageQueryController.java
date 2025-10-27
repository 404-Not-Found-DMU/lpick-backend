package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import com.notfound.lpickbackend.wiki.query.dto.response.PopularItemResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageTitleResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import com.notfound.lpickbackend.wiki.query.service.PopularityService;
import com.notfound.lpickbackend.wiki.query.service.WikiDomainQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "위키 페이지 조회 컨트롤러", description = "위키 페이지 조회 관련 컨트롤러")
public class WikiPageQueryController {

    private final WikiDomainQueryService wikiDomainQueryService;
    private final PopularityService popularityService;
    private static final Logger viewLog = LoggerFactory.getLogger("view-logger");

    // 현재로서는, id 값을 알아야 접근 가능.
    // 검색창에 wikiPage가 지닌 title 기입 시 유사한 목록 제공하거나, 특정 대상 wikiPage 제공 필요(추후 검색과 함께 구현할 것)
    // 동일한 이름의 문서가 존재할 수 있다. 이 경우 인지도 기반으로 먼저 제공될 수 있도록 별도 구현 필요? 리스트 형식 반환 페이지 구현?
    @GetMapping("/wiki/{wikiId}")
    @Operation(summary = "위키 상세 조회", description = "위키 문서 상세 조회 기능")
    public ResponseEntity<WikiPageViewResponse> getWikiPageView(
            @PathVariable("wikiId")String wikiId,
            @RequestParam("dummyUserId")String userId
    ) {

        WikiPageViewResponse wikiPageViewResponse = wikiDomainQueryService.getWikiPageView(wikiId, userId);

        // 위키 상세 조회시 로그 생성
        viewLog.info("{\"type\": \"{}\", \"id\": \"{}\"}", wikiPageViewResponse.getWikiPageClass(), wikiId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(wikiPageViewResponse);
    }

    // 최근에 수정된 문서 10개를 집계하여 제공.
    // requestparam 기반의 분류별 집계는 실제 분류 기준 설정 후 구현하기
    // ex. 분류가 단순히 앨범/기기/가수? 아니면 재즈/힙합/밴드 등으로 상세 구분?
    @GetMapping("/wiki/recent-modify")
    @Operation(summary = "최근 수정 위키 조회", description = "최근 수정 된 10개의 위키 조회 기능")
    public ResponseEntity<List<WikiPageTitleResponse>> getRecentlyModifiedWikiPageList(
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wikiDomainQueryService.getRecentlyModifiedWikiPageList(10, Instant.now()));
    }

    @Operation(
            summary = "인기 위키 항목 조회",
            description = "최근 1시간 조회 로그를 기준으로 인기 항목을 리턴합니다."
    )
    @GetMapping("/popular")
    public ResponseEntity<List<PopularItemResponse>> getPopularWikis(
            @Parameter(description = "항목 타입", required = true, example = "ALBUM")
            @RequestParam WikiPageClass type,
            @Parameter(description = "가져올 개수", example = "5")
            @RequestParam(defaultValue = "5") int size
    ) {
        // Service는 String을 받으니 enum → name()으로 전달
        List<PopularItemResponse> result = popularityService.getPopularWikis(type.name(), size);
        return ResponseEntity.ok(result);
    }
}
