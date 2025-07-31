package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreQueryRepository extends JpaRepository<Genre, String> {
}
