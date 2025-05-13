package com.notfound.lpickbackend.Wiki.Query.Controller;

import com.notfound.lpickbackend.Wiki.Query.Service.WikiDiffServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class PageRevisionQueryController {
    private final WikiDiffServiceV2 WikiDiffServiceV2;

    @GetMapping("/pagerevision-difference/{wikiId}")
    public ResponseEntity<String> getDiffLineHtml(@PathVariable("wikiId") String wikiId,
                                                                         @RequestParam("old") String oldVersion,
                                                                         @RequestParam("new") String newVersion) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(WikiDiffServiceV2.getTwoRevisionDiffHtml(wikiId, oldVersion, newVersion));
    }

}
