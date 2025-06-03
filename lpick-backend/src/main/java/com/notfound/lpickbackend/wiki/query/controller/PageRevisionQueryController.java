package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.logic.WikiDiffServiceV2;
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
public class PageRevisionQueryController {
    private final WikiDiffServiceV2 WikiDiffServiceV2;
    private final PageRevisionQueryService pageRevisionQueryService;

    @GetMapping("/wiki/{wikiId}/revision")
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
    public ResponseEntity<PageRevisionResponse> getPageRevision(
            @PathVariable("wikiId") String wikiId,
            @PathVariable("version") String version
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pageRevisionQueryService.getPageRevisionResponse(wikiId, version));
    }

    @GetMapping("/wiki/{wikiId}/revision/difference")
    public ResponseEntity<String> getDiffLineHtml(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("old") String oldVersion,
            @RequestParam("new") String newVersion
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(WikiDiffServiceV2.getTwoRevisionDiffHtml(wikiId, oldVersion, newVersion));
    }

}
