package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiPageCommandRepository extends JpaRepository<WikiPage, String> {
}
