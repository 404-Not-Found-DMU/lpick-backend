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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class DataSyncService { // AlbumSyncService에서 이름 변경

    //--- JPA Repositories
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

    /** Document 클래스에 설정 된 Setting, Mapping을 적용한다. */
    private void recreateIndex(Class<?> docClass) {
        IndexOperations io = operations.indexOps(docClass);
        if (io.exists()) io.delete();
        io.create();                             // @Setting 적용
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
        recreateAllIndices();        // 1) 인덱스/매핑 먼저
        syncAllAlbums();             // 2) 그 다음 색인
        syncAllArtists();
        syncAllGears();
        syncAllArticles();
        syncAllWikiPage();
    }

    @Transactional(readOnly = true)
    protected <T, D> void syncAllData(
            PagingAndSortingRepository<T, ?> jpaRepository,
            ElasticsearchRepository<D, ?> elasticsearchRepository,
            Function<T, D> documentMapper,
            String entityName
    ) {
        int pageNumber = 0;

        while (true) {
            // 1. 페이지 단위로 데이터 조회
            int BATCH_SIZE = 5000;
            PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
            Page<T> entityPage = jpaRepository.findAll(pageRequest);

            List<D> documents = entityPage.getContent().stream()
                    .map(documentMapper) // 매개변수로 받은 매핑 함수 사용
                    .collect(Collectors.toList());

            if (documents.isEmpty()) {
                System.out.println(String.format("'%s' 동기화 완료: 마지막 페이지 (페이지 %d)", entityName, pageNumber));
                break; // 데이터가 없으면 종료
            }

            // 2. ElasticSearch에 배치 저장
            elasticsearchRepository.saveAll(documents);

            System.out.println(String.format("'%s' 페이지 %d 동기화 완료 (요소 수: %d)", entityName, pageNumber, documents.size()));

            if (!entityPage.hasNext()) {
                break; // 마지막 페이지 처리 완료
            }

            pageNumber++;

            // 3. (선택적) 요청 사이에 잠시 대기하여 부하 줄이기
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    //--- Public API: 범용 메서드 호출 ---

    public void syncAllAlbums() {
        syncAllData(albumQueryRepository, albumDocumentRepository, AlbumDocument::from, "Album");
    }

    public void syncAllArtists() {
        syncAllData(artistQueryRepository, artistDocumentRepository, ArtistDocument::from, "Artist");
    }

    public void syncAllGears() {
        syncAllData(gearQueryRepository, gearDocumentRepository, GearDocument::from, "Gear");
    }

    public void syncAllArticles() {
        syncAllData(articleQueryRepository, articleDocumentRepository, ArticleDocument::from, "Article");
    }
    public void syncAllWikiPage() {
        syncAllData(wikiPageQueryRepository, wikiPageDocumentRepository, WikiPageDocument::from, "WikiPage");
    }

    /**
     * 앨범이 저장/수정될 때 Elasticsearch에 반영합니다.
     */
    public void syncAlbum(Album album) {
        AlbumDocument document = AlbumDocument.from(album);
        albumDocumentRepository.save(document);
    }

    /**
     * 앨범이 삭제될 때 Elasticsearch에서도 제거합니다.
     */
    public void deleteAlbum(String albumId) {
        albumDocumentRepository.deleteById(albumId);
    }

    /**
     * 아티스트가 저장/수정될 때 Elasticsearch에 반영합니다.
     */
    public void syncArtist(Artist artist) {
        ArtistDocument document = ArtistDocument.from(artist);
        artistDocumentRepository.save(document);
    }

    /**
     * 아티스트가 삭제될 때 Elasticsearch에서도 제거합니다.
     */
    public void deleteArtist(String artistId) {
        artistDocumentRepository.deleteById(artistId);
    }

    /**
     * 기어가 저장/수정될 때 Elasticsearch에 반영합니다.
     */
    public void syncGear(Gear gear) {
        GearDocument document = GearDocument.from(gear);
        gearDocumentRepository.save(document);
    }

    /**
     * 기어가 삭제될 때 Elasticsearch에서도 제거합니다.
     */
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

    /**
     * 기어가 저장/수정될 때 Elasticsearch에 반영합니다.
     */
    public void syncExpertRequest(ExpertRequest expertRequest) {
        ExpertRequestDocument document = ExpertRequestDocument.from(expertRequest);
        expertRequestDocumentRepository.save(document);
    }

    /**
     * 기어가 삭제될 때 Elasticsearch에서도 제거합니다.
     */
    public void deleteExpertRequest(String expertRequestId) {
        expertRequestDocumentRepository.deleteById(expertRequestId);
    }
}

