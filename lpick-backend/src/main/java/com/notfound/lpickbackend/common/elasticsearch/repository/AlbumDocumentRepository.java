package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumDocumentRepository extends ElasticsearchRepository<AlbumDocument, String> {

    // 앨범 이름으로 검색하는 메서드 (부분 일치 검색을 위해 QueryDSL/Criteria 사용 가능)
    List<AlbumDocument> findByNameContaining(String name);
}
