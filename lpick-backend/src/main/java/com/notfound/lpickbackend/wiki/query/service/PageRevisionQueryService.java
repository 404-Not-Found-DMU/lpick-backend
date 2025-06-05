package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.userInfo.query.dto.response.UserIdNamePairResponse;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.query.repository.PageRevisionQueryRepository;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageRevisionQueryService {
    private final PageRevisionQueryRepository pageRevisionQueryRepository;

    public PageRevisionResponse getPageRevisionResponse(String wikiId, String version) {

        return this.toResponseDTO(
                pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionNumber(wikiId, version)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION))
        );
    }

    /** PageResponse entity를 findAll한 결과의 ResponseDTO 매핑된 페이징 객체 제공. */
    public Page<PageRevisionResponse> getPageRevisionResponseList(Pageable pageable, String wikiId) {

        return pageRevisionQueryRepository.findAllByWiki_WikiId(wikiId, pageable)
                .map(this::toResponseDTO);
    }

    public List<PageRevision> readTwoRevision(String wikiId, String oldVersion, String newVersion) {
        PageRevision oldRevision = pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionNumber(wikiId, oldVersion)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));
        PageRevision newRevision = pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionNumber(wikiId, newVersion)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));


        return Arrays.asList(oldRevision, newRevision);
    }

    /** 가장 최근 수정된 10개 리비전을 추출하되, 하나의 위키페이지만 추출.
     * 즉, 가장 최근 수정된 10개의 위키페이지를 추출하는 목적이나, wikiPage는 modifiedAt과 같은 필드 지니지 않으므로
     * createdAt을 지니는 PageRevision에 대해 접근하여 최근 수정 위키페이지를 얻는다.
     *
     * 추후 추가 필요사항 : wikiStatus가 OPEN인 revision들만 불러와야한다.
     */
    public Page<PageRevision> getLatestRevisionPerWiki(Pageable pageable) {
        // 1) 먼저 “각 위키 ID별로 최신 순서대로 페이지”를 뽑아 올 수 있는 Repository 메서드
        Page<String> wikiIdPage =
                pageRevisionQueryRepository.findWikiIdsOrderByLatestRevision(pageable);

        // 2) 그 Page<String> 객체 안에는
        //    - 콘텐츠: List<String> (wikiId 리스트; 이미 최신순으로 정렬되어 있음)

        List<String> wikiIds = wikiIdPage.getContent();
        if (wikiIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3) 이제 2단계로 “각 위키별 최신 리비전 1건”을 페이징해서 가져옵니다.
        return pageRevisionQueryRepository.findLatestRevisionsForWikis(wikiIds, pageable);
    }

    // 중복되고 너무 길어져서 가독성 획득 위해 메소드로 분리
    private PageRevisionResponse toResponseDTO(PageRevision entity) {
        return PageRevisionResponse.builder()
                .revisionId(entity.getRevisionId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .createWho(UserIdNamePairResponse.builder()
                        .oauthId(entity.getUserInfo().getOauthId())
                        .nickName(entity.getUserInfo().getNickname())
                        .build())
                .build();
    }

    public PageRevision findByPageRevision_revisionNumberAndWiki_wikiId(String revisionNumber, String wikiId) {
        return pageRevisionQueryRepository.findByrevisionNumberAndWiki_wikiId(
                revisionNumber,
                wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));
    }
    public PageRevision getPageRevisionById(String revisionId) {
        return pageRevisionQueryRepository.findById(revisionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));
    }

    public long wikicountByWiki_WikiId(String wikiId) {
        return pageRevisionQueryRepository.countByWiki_WikiId(wikiId);
    }
}
