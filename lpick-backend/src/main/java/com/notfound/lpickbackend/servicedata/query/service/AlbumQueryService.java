package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumQueryService {

    private final AlbumQueryRepository albumQueryRepository;

    public Album getAlbumById(String albumId) {
        return albumQueryRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ALBUM));
    }
}
