package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.ExpertRequestDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertRequestDocumentRepository extends ElasticsearchRepository<ExpertRequestDocument, String> {
}
