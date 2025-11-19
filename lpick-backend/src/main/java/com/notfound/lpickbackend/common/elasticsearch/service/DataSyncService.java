package com.notfound.lpickbackend.common.elasticsearch.service;

import com.notfound.lpickbackend.common.elasticsearch.document.*;
import com.notfound.lpickbackend.common.elasticsearch.repository.*;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.servicedata.command.application.domain.Artist;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import com.notfound.lpickbackend.servicedata.query.repository.ArtistQueryRepository;
import com.notfound.lpickbackend.servicedata.query.repository.GearQueryRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSyncService { // AlbumSyncService에서 이름 변경

    //--- JPA / Query Repositories
    private final AlbumQueryRepository albumQueryRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final GearQueryRepository gearQueryRepository;
    private final ArticleQueryRepository articleQueryRepository;
    private final WikiPageQueryRepository wikiPageQueryRepository;

    //--- Elasticsearch Repositories
    private final AlbumDocumentRepository albumDocumentRepository;
    private final ArtistDocumentRepository artistDocumentRepository;
    private final GearDocumentRepository gearDocumentRepository;
    private final ArticleDocumentRepository articleDocumentRepository;
    private final WikiPageDocumentRepository wikiPageDocumentRepository;
    private final ExpertRequestDocumentRepository expertRequestDocumentRepository;

    private final ElasticsearchOperations operations;

    // ========================================
    // Index 재생성
    // ========================================

    /** Document 클래스에 설정 된 Setting, Mapping을 적용한다. */
    private void recreateIndex(Class<?> docClass) {
        IndexOperations io = operations.indexOps(docClass);
        if (io.exists()) io.delete();
        io.create();                               // @Setting 적용
        io.putMapping(io.createMapping(docClass)); // @Mapping 적용
    }

    public void recreateAllIndices() {
        recreateIndex(AlbumDocument.class);
        recreateIndex(ArtistDocument.class);
        recreateIndex(GearDocument.class);
        recreateIndex(ArticleDocument.class);
        recreateIndex(WikiPageDocument.class);
    }

    /** 초기화 + 전체 싱크 한 번에 */
    public void recreateAndSyncAll() {
        recreateAllIndices();  // 1) 인덱스/매핑 먼저
        syncAllAlbums();       // 2) 그 다음 색인
        syncAllArtists();
        syncAllGears();
        syncAllArticles();
        syncAllWikiPage();
    }

    // ========================================
    // 1. OFFSET 기반 범용 동기화 (소량 데이터용 - Article, WikiPage 등)
    // ========================================

    @Transactional(readOnly = true)
    protected <T, D> void syncAllDataOffset(
            PagingAndSortingRepository<T, ?> jpaRepository,
            ElasticsearchRepository<D, ?> elasticsearchRepository,
            Function<T, D> documentMapper,
            String entityName
    ) {
        int pageNumber = 0;
        final int BATCH_SIZE = 5000;

        while (true) {
            PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
            Page<T> entityPage = jpaRepository.findAll(pageRequest);

            List<D> documents = entityPage.getContent().stream()
                    .map(documentMapper)
                    .collect(Collectors.toList());

            if (documents.isEmpty()) {
                log.warn("'{}' 동기화 완료: 마지막 페이지 (페이지 {})", entityName, pageNumber);
                break;
            }

            elasticsearchRepository.saveAll(documents);

            log.warn("'{}' 페이지 {} 동기화 완료 (요소 수: {})",
                    entityName, pageNumber, documents.size());

            if (!entityPage.hasNext()) {
                log.warn("'{}' 더 이상 다음 페이지 없음. 동기화 종료.", entityName);
                break;
            }

            pageNumber++;

            try {
                Thread.sleep(500); // 부하 조절 (필요 시 조정/삭제)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ========================================
    // 2. Keyset 기반 범용 동기화 (대량 데이터용 - Album, Artist, Gear)
    // ========================================

    @Transactional(readOnly = true)
    protected <T, D> void syncAllDataKeyset(
            KeysetPageRepository<T> jpaRepository,
            ElasticsearchRepository<D, ?> elasticsearchRepository,
            Function<T, D> documentMapper,
            Function<T, String> idExtractor,   // 엔티티에서 ID 추출
            String entityName
    ) {
        final int BATCH_SIZE = 5000;
        String lastId = null; // 시작 커서
        int batchIndex = 0;

        Pageable pageable = PageRequest.of(0, BATCH_SIZE);

        while (true) {
            // 1. lastId 이후 데이터 조회 (Keyset)
            List<T> entities = jpaRepository.findNextPage(lastId, pageable);

            if (entities.isEmpty()) {
                log.warn("'{}' 동기화 완료: 마지막 배치 index={}", entityName, batchIndex);
                break;
            }

            // 2. 문서 변환
            List<D> documents = entities.stream()
                    .map(documentMapper)
                    .collect(Collectors.toList());

            // 3. ES 저장
            elasticsearchRepository.saveAll(documents);

            // 4. 다음 커서(lastId) 업데이트
            T lastEntity = entities.get(entities.size() - 1);
            lastId = idExtractor.apply(lastEntity);

            log.warn("'{}' 배치 {} 동기화 완료 (요소 수: {}, lastId: {})",
                    entityName, batchIndex, documents.size(), lastId);

            batchIndex++;

            try {
                Thread.sleep(500); // 부하 조절 (필요 시 조정/삭제)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ========================================
    // Public API: 엔티티별 동기화 엔트리 포인트
    // ========================================

    // --- 대용량: Keyset 사용 ---
    public void syncAllAlbums() {
        syncAllDataKeyset(
                albumQueryRepository,
                albumDocumentRepository,
                AlbumDocument::from,
                Album::getAlbumId,
                "Album"
        );
    }

    public void syncAllArtists() {
        syncAllDataKeyset(
                artistQueryRepository,
                artistDocumentRepository,
                ArtistDocument::from,
                Artist::getArtistId,
                "Artist"
        );
    }

    public void syncAllGears() {
        syncAllDataKeyset(
                gearQueryRepository,
                gearDocumentRepository,
                GearDocument::from,
                Gear::getGearId,   // 필드명에 맞게 수정
                "Gear"
        );
    }

    // --- 상대적으로 소량: 기존 OFFSET 방식 유지 ---
    public void syncAllArticles() {
        syncAllDataOffset(
                articleQueryRepository,
                articleDocumentRepository,
                ArticleDocument::from,
                "Article"
        );
    }

    public void syncAllWikiPage() {
        syncAllDataOffset(
                wikiPageQueryRepository,
                wikiPageDocumentRepository,
                WikiPageDocument::from,
                "WikiPage"
        );
    }

    // ========================================
    // 단건 Sync API (기존 그대로)
    // ========================================

    /** 앨범이 저장/수정될 때 Elasticsearch에 반영합니다. */
    public void syncAlbum(Album album) {
        AlbumDocument document = AlbumDocument.from(album);
        albumDocumentRepository.save(document);
    }

    /** 앨범이 삭제될 때 Elasticsearch에서도 제거합니다. */
    public void deleteAlbum(String albumId) {
        albumDocumentRepository.deleteById(albumId);
    }

    /** 아티스트가 저장/수정될 때 Elasticsearch에 반영합니다. */
    public void syncArtist(Artist artist) {
        ArtistDocument document = ArtistDocument.from(artist);
        artistDocumentRepository.save(document);
    }

    /** 아티스트가 삭제될 때 Elasticsearch에서도 제거합니다. */
    public void deleteArtist(String artistId) {
        artistDocumentRepository.deleteById(artistId);
    }

    /** 기어가 저장/수정될 때 Elasticsearch에 반영합니다. */
    public void syncGear(Gear gear) {
        GearDocument document = GearDocument.from(gear);
        gearDocumentRepository.save(document);
    }

    /** 기어가 삭제될 때 Elasticsearch에서도 제거합니다. */
    public void deleteGear(String gearId) {
        gearDocumentRepository.deleteById(gearId);
    }

    public void syncArticle(Article article) {
        ArticleDocument document = ArticleDocument.from(article);
        articleDocumentRepository.save(document);
    }

    public void deleteArticle(String articleId) {
        articleDocumentRepository.deleteById(articleId);
    }

    /** 전문가 요청 저장/수정 시 Elasticsearch 반영 */
    public void syncExpertRequest(ExpertRequest expertRequest) {
        ExpertRequestDocument document = ExpertRequestDocument.from(expertRequest);
        expertRequestDocumentRepository.save(document);
    }

    /** 전문가 요청 삭제 시 Elasticsearch에서도 제거 */
    public void deleteExpertRequest(String expertRequestId) {
        expertRequestDocumentRepository.deleteById(expertRequestId);
    }
}

