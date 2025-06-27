package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.wiki.command.application.dto.request.WikiPageCreateRequestDTO;
import com.notfound.lpickbackend.wiki.command.application.dto.request.WikiStatusRequest;
import com.notfound.lpickbackend.wiki.command.application.service.WikiDomainCommandService;
import com.notfound.lpickbackend.wiki.command.application.service.WikiPageCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "위키 페이지 컨트롤러", description = "위키 페이지 생성/수정/삭제 기능")
public class WikiPageCommandController {

    private final WikiDomainCommandService wikiDomainCommandService;
    private final WikiPageCommandService wikiPageCommandService;

    @PostMapping("/wiki")
    @Operation(summary = "위키 페이지 생성", description = "위키 페이지 최초 생성 기능")
    public ResponseEntity<SuccessCode> createWikiPage(
            @RequestBody WikiPageCreateRequestDTO wikiPageCreateRequestDTO
    ) {

        wikiDomainCommandService.createWikiPageAndRevision(wikiPageCreateRequestDTO);

        return ResponseEntity.ok(SuccessCode.CREATE_SUCCESS);
    }

    /**
     * 위키페이지 표출, 가리기, 삭제 기능 구현.
     *
     */
    @PatchMapping("/wiki/{wikiId}/status")
    @Operation(summary = "위키 페이지 상태 변경", description = "위키페이지 표출, 가리기 기능")
    //@PreAuthorize("hasAuthority('AUTH_ADMIN')")
    public ResponseEntity<SuccessCode> changeStatusWikiPage(
            @RequestBody @Valid WikiStatusRequest statusRequest,
            @PathVariable("wikiId") String wikiId
    ) {
        wikiPageCommandService.updateWikiPageStatus(wikiId, statusRequest);
        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    /**
     * 리비전 '되돌리기' 기능.
     * ex. r134가 최신인 위키에 대해 r132로 되돌리기 수행 시, r132내용 기반으로 r135를 만들어 새로 적용시키는 방식.
     */
    @PatchMapping("/wiki/{wikiId}/current-revision/{targetRevisionId}")
    @Operation(summary = "위키 버젼 롤백", description = "위키 페이지를 특정 버젼으로 되돌리는 기능")
    //@PreAuthorize("hasRole('TIER_SILVER')") // 임시 설정
    public ResponseEntity<SuccessCode> revertWikiPageRevision(
            @PathVariable("wikiId") String wikiId,
            @PathVariable("targetRevisionId") String targetRevisionId
    ) {
        wikiDomainCommandService.revertWikiPageAndRevision(wikiId, targetRevisionId);
        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    // @PreAuthorize("hasAuthority('AUTH_ADMIN')")
    @DeleteMapping("/wiki/{wikiId}")
    @Operation(summary = "관리자 위키 페이지 삭제", description = "관리자의 위키페이지 삭제 기능")
    public ResponseEntity<SuccessCode> hardDeleteWikiPage(
            @PathVariable("wikiId") String wikiId
    ) {
        wikiDomainCommandService.hardDeleteWikiPage(wikiId);
        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
