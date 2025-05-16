package com.notfound.lpickbackend.wikipage.command.application.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import com.notfound.lpickbackend.wikipage.command.repository.WikiPageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiPageCommandService {

    private final WikiPageCommandRepository wikiPageCommandRepository;

    public String createWikiPage(String title) {
        WikiPage newWikiPage = WikiPage.builder().title(title).build();

        return null;
    }
}
