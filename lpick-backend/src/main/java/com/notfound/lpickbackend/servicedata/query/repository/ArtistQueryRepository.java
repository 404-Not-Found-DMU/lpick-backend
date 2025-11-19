package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.common.elasticsearch.repository.KeysetPageRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistQueryRepository extends JpaRepository<Artist, String>, KeysetPageRepository<Artist> {

    @Override
    @Query("""
        SELECT a
        FROM Artist a
        WHERE (:lastId IS NULL OR a.artistId > :lastId)
        ORDER BY a.artistId ASC
        """)
    List<Artist> findNextPage(@Param("lastId") String lastId, Pageable pageable);

}

