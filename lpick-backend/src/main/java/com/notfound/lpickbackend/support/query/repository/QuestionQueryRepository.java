package com.notfound.lpickbackend.support.query.repository;


import com.notfound.lpickbackend.support.command.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionQueryRepository extends JpaRepository<Question, String> {
}
