package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumQueryRepository extends JpaRepository<Album, String> {

    @Query(value = "SELECT * FROM album WHERE lpti = :lpti ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<Album> findRandom5ByLpti(@Param("lpti") String lpti);

    List<Album> findTop5ByReleaseDateIsNotNullOrderByReleaseDateDesc();
}
