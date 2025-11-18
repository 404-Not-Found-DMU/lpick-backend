package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.userinfo.query.dto.response.UserIdNamePairResponse;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
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

    public PageRevisionResponse getPageRevisionResponse(String wikiId, String revisionId) {

        return this.toResponseDTO(
                pageRevisionQueryRepository
                .findByWiki_WikiIdAndRevisionId(wikiId, revisionId)
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
    public List<PageRevision> getLatestRevisionPerWiki(Pageable pageable) {
        return pageRevisionQueryRepository.findLatestRevisionsByCurrentRevision(pageable);
    }
    // 중복되고 너무 길어져서 가독성 획득 위해 메소드로 분리
    private PageRevisionResponse toResponseDTO(PageRevision entity) {
        return PageRevisionResponse.builder()
                .revisionId(entity.getRevisionId())
                .revisionNumber(entity.getRevisionNumber())
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

    public int countRevisionByOauthId(String oauthId) {
        return pageRevisionQueryRepository.countByUserInfo_OauthId(oauthId);
    }
}
