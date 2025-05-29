package com.notfound.lpickbackend.wikipage.command.application.service;

import com.notfound.lpickbackend.wikipage.command.repository.WikiBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiBookmarkCommandService {
    private final WikiBookmarkRepository wikiBookmarkRepository;


    public void deleteBookmarkDataByWiki_WikiId(String wikiId) {
        wikiBookmarkRepository.deleteAllByWiki_WikiId(wikiId);
    }
}
