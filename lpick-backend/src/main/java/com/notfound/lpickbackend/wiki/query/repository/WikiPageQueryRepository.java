package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WikiPageQueryRepository extends JpaRepository<WikiPage, String> {

}
