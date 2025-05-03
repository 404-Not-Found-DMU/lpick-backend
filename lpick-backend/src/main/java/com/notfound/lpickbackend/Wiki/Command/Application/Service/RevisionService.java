package com.notfound.lpickbackend.Wiki.Command.Application.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.InlineDiffLine;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Request.PageRevisionRequest;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.Response.PageRevisionResponse;
import com.notfound.lpickbackend.Wiki.Command.Repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionRepository revisionRepository;

    private final WikiDiffService wikiDiffService;

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
    
    // 추후 확장 가능성 있어 별도 메소드로 명기
    // ex. v1 ~ v5 까지의 diff를 반복하여 수행하기 등
    public List<InlineDiffLine> findRevisionDiff(String wikiId,
                                                 String oldVersionNumber,
                                                 String newVersionNumber) {

        PageRevision oldRevision = revisionRepository
                .findByWikiIdAndRevisionNumber(wikiId, oldVersionNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리비전입니다."));
        PageRevision newRevision = revisionRepository
                .findByWikiIdAndRevisionNumber(wikiId, newVersionNumber)   // ← 여기를 newVersionNumber 로!
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리비전입니다."));

        List<String> oldLines = Arrays.stream(oldRevision.getContent().split("\n"))
                .map(line -> line.replace("\\n","").trim())
                .toList();
        List<String> newLines = Arrays.stream(newRevision.getContent().split("\n"))
                .map(line -> line.replace("\\n","").trim())
                .toList();

        return wikiDiffService.computeInlineDiff(oldLines, newLines);
    }

}
