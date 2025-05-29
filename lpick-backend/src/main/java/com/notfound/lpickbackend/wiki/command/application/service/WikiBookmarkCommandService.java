package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.wiki.command.repository.WikiBookmarkCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiBookmarkCommandService {
    private final WikiBookmarkCommandRepository wikiBookmarkCommandRepository;


    public void deleteBookmarkDataByWiki_WikiId(String wikiId) {
        wikiBookmarkCommandRepository.deleteAllByWiki_WikiId(wikiId);
    }
}
