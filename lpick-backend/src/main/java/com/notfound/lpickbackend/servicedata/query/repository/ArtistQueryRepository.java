package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistQueryRepository extends JpaRepository<Artist, String> {

    /**
     * 사용처 : 1. 마이페이지에서 lp 관리 도중 lp에 표기할 artist 명칭 확인 위함.
     *
     */
    @Query("")
    public findArtistWithArtistAlbumRelationShipByAlbumId()}

