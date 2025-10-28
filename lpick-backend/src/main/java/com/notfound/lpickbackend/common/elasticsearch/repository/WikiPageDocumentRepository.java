package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.WikiPageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface WikiPageDocumentRepository extends ElasticsearchRepository<WikiPageDocument, String> {
}
