package com.notfound.lpickbackend.Wiki.Query.Repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WikiPageQueryRepository extends JpaRepository<WikiPage, String> {

}
