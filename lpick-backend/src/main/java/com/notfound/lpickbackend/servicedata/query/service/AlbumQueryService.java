package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.repository.AlbumDocumentRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumQueryService {

    private final AlbumQueryRepository albumQueryRepository;
    private final AlbumDocumentRepository albumDocumentRepository;

    @Transactional(readOnly = true)
    public Album getAlbumById(String albumId) {
        return albumQueryRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ALBUM));
    }

    /**
     * 앨범 이름으로 간단히 검색합니다 (Spring Data Repository 메서드 사용).
     */
    public List<AlbumDocument> searchAlbumsByName(String name) {
        return albumDocumentRepository.findByNameContaining(name);
    }
}
