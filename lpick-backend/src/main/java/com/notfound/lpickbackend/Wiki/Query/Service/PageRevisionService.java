package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import com.notfound.lpickbackend.Wiki.Query.Repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageRevisionService {
    private final RevisionRepository revisionRepository;

    public List<PageRevision> getTwoRevision(String wikiId, String oldVersion, String newVersion) {
        PageRevision oldRevision = revisionRepository
                .findByWikiIdAndRevisionNumber(wikiId, oldVersion)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리비전입니다."));
        PageRevision newRevision = revisionRepository
                .findByWikiIdAndRevisionNumber(wikiId, newVersion)   // ← 여기를 newVersionNumber 로!
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리비전입니다."));


        return Arrays.asList(oldRevision, newRevision);
    }
}
