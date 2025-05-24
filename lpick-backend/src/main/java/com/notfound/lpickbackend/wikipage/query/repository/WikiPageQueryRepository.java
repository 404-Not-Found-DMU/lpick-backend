package com.notfound.lpickbackend.wikipage.query.repository;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiPageQueryRepository extends JpaRepository<WikiPage, String> {
}
