package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.dto.ImageSearchResponse;
import com.notfound.lpickbackend.servicedata.query.dto.ImageSearchResult;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResultWithImage;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WikiPageQueryService {
    private final WikiPageQueryRepository wikiPageQueryRepository;

    @Transactional(readOnly = true)
    public WikiPage getWikiPageById(String wikiId) {
        return wikiPageQueryRepository.findById(wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));
    }

    public boolean isExsistsById(String wikiId) {
        return wikiPageQueryRepository.existsById(wikiId);
    }

    // 재정신으로 만든게 아님
    @Transactional(readOnly = true)
    public List<SearchResultWithImage> findByImage(ImageSearchResponse result) {

        // python 검색 결과 map으로 저장
        Map<String, ImageSearchResult> resultMap = result.getResults().stream()
                .filter(item -> item.getRelease_id() != null)
                .collect(Collectors.toMap(
                        ImageSearchResult::getRelease_id,
                        Function.identity(),
                        (a, b) -> a.getSimilarity() >= b.getSimilarity() ? a : b
                ));

        // results에서 id 뽑아오기
        List<String> ids = result.getResults().stream()
                .map(ImageSearchResult::getRelease_id)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        // wikiPage 불러오기
        List<WikiPage> wikiPages = wikiPageQueryRepository.findWikiByAlbumIdsIn(ids);

        List<SearchResultWithImage> results = new ArrayList<>();

        // 반환값 저장
        for (WikiPage wikiPage : wikiPages) {
            results.add(new SearchResultWithImage(wikiPage, resultMap.get(wikiPage.getAlbum().getAlbumId())));
        }

        return results;
    }

    public WikiPage getRandomWikiPage() {
        double randomVal = Math.random();

        return wikiPageQueryRepository.findFirstByRandomPointGreaterThanEqualOrderByRandomPointAsc(randomVal)
                .orElseGet(() -> wikiPageQueryRepository.findFirstByOrderByRandomPointAsc()
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI)));
    }
}
