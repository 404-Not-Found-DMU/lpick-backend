package com.notfound.lpickbackend.Wiki.Command.Repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRevisionCommandRepository extends JpaRepository<PageRevision, String> {
}
