package com.notfound.lpickbackend.common.elasticsearch.service;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.repository.AlbumDocumentRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumSyncService {

    private final AlbumQueryRepository albumQueryRepository; // JPA Repository
    private final AlbumDocumentRepository albumDocumentRepository; // Elasticsearch Repository

    /**
     * RDB의 모든 앨범 데이터를 Elasticsearch로 초기 동기화합니다.
     */
    @Transactional(readOnly = true)
    public void syncAllAlbums() {
        final int BATCH_SIZE = 500; // 500개씩 묶어서 처리 (적절히 조절 필요)
        int pageNumber = 0;

        while (true) {
            // 1. 페이지 단위로 데이터 조회
            PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
            Page<Album> albumPage = albumQueryRepository.findAll(pageRequest);

            List<AlbumDocument> documents = albumPage.getContent().stream()
                    .map(AlbumDocument::from)
                    .collect(Collectors.toList());

            // 2. ElasticSearch에 배치 저장
            albumDocumentRepository.saveAll(documents);

            System.out.println(String.format("페이지 %d 동기화 완료 (요소 수: %d)", pageNumber, documents.size()));

            if (!albumPage.hasNext()) {
                break; // 마지막 페이지 처리 완료
            }

            pageNumber++;

            // 3. (선택적) 요청 사이에 잠시 대기하여 부하 줄이기
            try {
                Thread.sleep(500); // 500ms 지연 (클러스터 상태에 따라 조절)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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
}
