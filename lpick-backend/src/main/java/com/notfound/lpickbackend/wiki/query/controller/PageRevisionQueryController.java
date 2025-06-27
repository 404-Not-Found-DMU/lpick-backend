package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.logic.WikiDiffServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@Tag(name = "위키 버전 조회 컨트롤러", description = "위키 버전 관련 조회 기능 컨트롤러")
public class PageRevisionQueryController {
    private final WikiDiffServiceV2 WikiDiffServiceV2;
    private final PageRevisionQueryService pageRevisionQueryService;

    @GetMapping("/wiki/{wikiId}/revision")
    @Operation(summary = "위키 버전 리스트 조회", description = "특정 위키의 버전 리스트 조회 기능")
    public ResponseEntity<Page<PageRevisionResponse>> getPageRevisionList(
            @PathVariable("wikiId") String wikiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Pattern(
                    regexp = "createdAt,(asc|desc)",
                    message = "sort 기본값은 createdAt,desc(최신순)입니다. sort는 'createdAt,asc', 'createdAt,desc' 중 하나거나 존재하지 않아야 합니다."
            )
            @RequestParam(required = false) String sortParam
    ) {
        Sort sort;
        if(sortParam == null) {
            sort = Sort.by("createdAt").descending();
        }
        else {
            String[] sortParamArr = sortParam.split(",");

            if("asc".equals(sortParamArr[1])) sort = Sort.by(sortParamArr).ascending();
            else sort = Sort.by(sortParamArr).descending();
        }

        Page<PageRevisionResponse> pageRevisionList = pageRevisionQueryService.getPageRevisionResponseList(PageRequest.of(page, size, sort), wikiId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pageRevisionList);
    }

    @GetMapping("/wiki/{wikiId}/revision/{version}")
    @Operation(summary = "위키 버전 조회", description = "위키의 특정 버전 조회 기능")
    public ResponseEntity<PageRevisionResponse> getPageRevision(
            @PathVariable("wikiId") String wikiId,
            @PathVariable("version") String version
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pageRevisionQueryService.getPageRevisionResponse(wikiId, version));
    }

    @GetMapping("/wiki/{wikiId}/revision/difference")
    @Operation(summary = "위키 버전 비교", description = "위키 버전간의 차이를 비교하는 기능")
    public ResponseEntity<String> getDiffLineHtml(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("old") String oldVersion,
            @RequestParam("new") String newVersion
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(WikiDiffServiceV2.getTwoRevisionDiffHtml(wikiId, oldVersion, newVersion));
    }

}
