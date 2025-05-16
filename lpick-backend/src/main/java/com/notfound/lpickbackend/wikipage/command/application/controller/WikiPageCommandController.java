package com.notfound.lpickbackend.wikipage.command.application.controller;

import com.notfound.lpickbackend.wikipage.command.application.dto.WikiPageCreateRequestDTO;
import com.notfound.lpickbackend.wikipage.command.application.service.WikiPageAndRevisionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WikiPageCommandController {

    private final WikiPageAndRevisionCommandService wikiPageAndRevisionCommandService;

    @PostMapping("WikiPage")
    public ResponseEntity<?> createWikiPage(
            @RequestBody WikiPageCreateRequestDTO wikiPageCreateRequestDTO
    ) {

        wikiPageAndRevisionCommandService.createWikiPageAndRevision(wikiPageCreateRequestDTO);

        return ResponseEntity.ok().build();
    }
}
