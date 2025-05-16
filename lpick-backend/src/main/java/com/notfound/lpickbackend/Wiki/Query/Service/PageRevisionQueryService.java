package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import com.notfound.lpickbackend.Wiki.Query.Repository.PageRevisionQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageRevisionQueryService {
    private final PageRevisionQueryRepository pageRevisionQueryRepository;

    public List<PageRevision> readTwoRevision(String wikiId, String oldVersion, String newVersion) {
        PageRevision oldRevision = pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionNumber(wikiId, oldVersion)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));
        PageRevision newRevision = pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionNumber(wikiId, newVersion)   // ← 여기를 newVersionNumber 로!
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));


        return Arrays.asList(oldRevision, newRevision);
    }
}
