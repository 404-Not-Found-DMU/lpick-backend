package com.notfound.lpickbackend.wikipage.command.application.controller;

import com.notfound.lpickbackend.wikipage.command.application.dto.WikiPageCreateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class WikiPageCommandController {

    @PostMapping("WikiPage")
    public ResponseEntity<?> createWikiPage(WikiPageCreateRequestDTO requestDTO) {

        return ResponseEntity.ok().build();
    }
}
