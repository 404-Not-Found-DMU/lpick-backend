package com.notfound.lpickbackend.support.command.repository;

import com.notfound.lpickbackend.support.command.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCommandRepository extends JpaRepository<Notice, String> {
}
