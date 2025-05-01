package com.notfound.lpickbackend.Wiki.Command.Application.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Request.PageRevisionRequest;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Response.PageRevisionResponse;
import com.notfound.lpickbackend.Wiki.Command.Repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionRepository revisionRepository;

    public PageRevisionResponse writeNewRevision(PageRevisionRequest request, String userId) {

        // 기존 버전 + 1 하기 위한 리비전 개수 카운팅
        // count vs 제일 높은값 1개 뽑기 중 하나 sql문 효율성 비교 필요
        long revisionNumber = revisionRepository.countByWikiId(request.getWikiId());
        log.info("number : " + revisionNumber);
        
        // entity 저장하여 id, createdAt 기입된채로 가져오기
        PageRevision saveResult = revisionRepository.save(PageRevision.builder()
                .revisionNumber("버전전치사" + (revisionNumber + 1))
                .content(request.getContent())
                .wikiId(request.getWikiId())
                .oauthId(userId)
                .build());

        // entity 기반 변경.
        // 원래라면 mapper 사용해야하나, 외부 라이브러리 쓸지 결정해야하므로 일단 빌더로 기입.
        return PageRevisionResponse.builder()
                .revisionId(saveResult.getRevisionId())
                .content(saveResult.getContent())
                .createdAt(saveResult.getCreatedAt())
                .createWho(saveResult.getOauthId())
                .build();
    }
}
