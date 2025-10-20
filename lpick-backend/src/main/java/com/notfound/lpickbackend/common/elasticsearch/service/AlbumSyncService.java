package com.notfound.lpickbackend.common.elasticsearch.service;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.repository.AlbumDocumentRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import lombok.RequiredArgsConstructor;
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
        List<Album> albums = albumQueryRepository.findAll();
        List<AlbumDocument> documents = albums.stream()
                .map(AlbumDocument::from) // Document 변환
                .collect(Collectors.toList());

        albumDocumentRepository.saveAll(documents);
        System.out.println(String.format("%d개의 앨범을 Elasticsearch에 동기화했습니다.", documents.size()));
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
