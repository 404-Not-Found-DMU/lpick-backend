package com.notfound.lpickbackend.wikipage.command.application.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import com.notfound.lpickbackend.wikipage.command.application.domain.Status;
import com.notfound.lpickbackend.wikipage.command.repository.WikiPageCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WikiPageCommandService {

    private final WikiPageCommandRepository wikiPageCommandRepository;

    @Transactional
    public String createWikiPage(String title) {

        log.warn(title);
        WikiPage newWikiPage = WikiPage.builder().
                title(title).
                currentRevision("r1"). // 초기 생성시 r1이라고 가정
                        status(Status.OPEN).
                build();

        wikiPageCommandRepository.save(newWikiPage);

        return newWikiPage.getWikiId();
    }
}
