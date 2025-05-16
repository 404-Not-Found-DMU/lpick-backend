package com.notfound.lpickbackend.wikipage.command.repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiPageCommandRepository extends JpaRepository<WikiPage, String> {
}
