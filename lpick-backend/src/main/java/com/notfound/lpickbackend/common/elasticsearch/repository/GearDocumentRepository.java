package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.GearDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GearDocumentRepository extends ElasticsearchRepository<GearDocument, String> {
}
