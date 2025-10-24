package com.notfound.lpickbackend.common.elasticsearch.repository;

import com.notfound.lpickbackend.common.elasticsearch.document.ArtistDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArtistDocumentRepository extends ElasticsearchRepository<ArtistDocument, String> {


}
