package com.notfound.lpickbackend.servicedata.query.service;

import com.notfound.lpickbackend.servicedata.command.domain.Genre;
import com.notfound.lpickbackend.servicedata.query.repository.GenreQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreQueryService {
    private final GenreQueryRepository genreQueryRepository;

    public List<String> getAllGenreList() {
        List<Genre> genreList = genreQueryRepository.findAll();
        return genreList.stream().map(Genre::getName).toList();
    }

}
