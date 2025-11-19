package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.common.elasticsearch.repository.KeysetPageRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GearQueryRepository extends JpaRepository<Gear, String>, KeysetPageRepository<Gear> {

    @Override
    @Query("""
        SELECT g
        FROM Gear g
        WHERE (:lastId IS NULL OR g.gearId > :lastId)
        ORDER BY g.gearId ASC
        """)
    List<Gear> findNextPage(@Param("lastId") String lastId, Pageable pageable);
}
