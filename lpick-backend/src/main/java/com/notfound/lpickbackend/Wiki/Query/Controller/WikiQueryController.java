package com.notfound.lpickbackend.Wiki.Query.Controller;

import com.notfound.lpickbackend.Wiki.Command.Application.DTO.InlineDiffLine;
import com.notfound.lpickbackend.Wiki.Command.Application.Service.RevisionService;
import com.notfound.lpickbackend.Wiki.Query.DTO.HighLightedDiffLine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class WikiQueryController {
    private final RevisionService revisionService;


    @GetMapping("/test-diff/{wikiId}")
    public ResponseEntity<List<InlineDiffLine>> getDiffLine(@PathVariable("wikiId") String wikiId,
                                                      @RequestParam("old") String oldVersion,
                                                      @RequestParam("new") String newVersion) {

        List<InlineDiffLine> diffResult = revisionService.findRevisionDiff(wikiId, oldVersion, newVersion);
        return ResponseEntity.status(HttpStatus.OK).body(diffResult);
    }

    @GetMapping("/test-diff-by-char/{wikiId}")
    public ResponseEntity<List<HighLightedDiffLine>> getDiffLineWithWord(@PathVariable("wikiId") String wikiId,
                                                            @RequestParam("old") String oldVersion,
                                                            @RequestParam("new") String newVersion) {

        List<HighLightedDiffLine> diffResult = revisionService.findRevisionDiffByWord(wikiId, oldVersion, newVersion);
        return ResponseEntity.status(HttpStatus.OK).body(diffResult);
    }
}
