package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.GearDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GearDocumentRepository extends ElasticsearchRepository<GearDocument, String> {
}
