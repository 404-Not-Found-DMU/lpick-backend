package com.notfound.lpickbackend.debate.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebateQueryRepository extends JpaRepository<Debate, String> {
}
