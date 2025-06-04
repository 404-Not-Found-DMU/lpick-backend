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

    public Page<PageRevision> getRecentlyCreatedPageRevision(Pageable pageable) {
        return pageRevisionQueryRepository.findLatestRevisionPerWiki(pageable);
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

    public PageRevision getPageRevisionById(String revisionId) {
        return pageRevisionQueryRepository.findById(revisionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVISION));
    }

    public long wikicountByWiki_WikiId(String wikiId) {
        return pageRevisionQueryRepository.countByWiki_WikiId(wikiId);
    }
}
