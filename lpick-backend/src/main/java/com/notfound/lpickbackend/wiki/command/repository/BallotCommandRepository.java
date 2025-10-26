package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Ballot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BallotCommandRepository extends JpaRepository<Ballot, String> {


    boolean existsByDebate_DtIdAndOauth_OauthId(String debateId, String userId);


}
