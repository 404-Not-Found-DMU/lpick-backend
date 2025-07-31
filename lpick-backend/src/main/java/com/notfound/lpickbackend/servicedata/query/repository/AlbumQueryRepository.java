package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumQueryRepository extends JpaRepository<Album, String> {
}
