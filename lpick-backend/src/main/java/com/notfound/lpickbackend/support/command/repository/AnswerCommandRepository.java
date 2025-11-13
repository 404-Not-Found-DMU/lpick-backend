package com.notfound.lpickbackend.support.command.repository;

import com.notfound.lpickbackend.support.command.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerCommandRepository extends JpaRepository<Answer, String> {
}
