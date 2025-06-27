package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.servicedata.command.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistQueryRepository extends JpaRepository<Artist, String> {

}

