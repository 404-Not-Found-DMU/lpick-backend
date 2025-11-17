package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDocumentRepository extends ElasticsearchRepository<ArticleDocument, String> {
}
