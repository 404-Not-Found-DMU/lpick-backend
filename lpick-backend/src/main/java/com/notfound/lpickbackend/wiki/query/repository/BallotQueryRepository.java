package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Ballot;
import com.notfound.lpickbackend.wiki.query.dto.BallotCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BallotQueryRepository extends JpaRepository<Ballot, String> {

    @Query("""
    select new com.notfound.lpickbackend.wiki.query.dto.BallotCount(
  coalesce(sum(case when b.ballotValue = com.notfound.lpickbackend.wiki.command.application.domain.BallotValue.AGREE    then 1L else 0L end), 0L),
  coalesce(sum(case when b.ballotValue = com.notfound.lpickbackend.wiki.command.application.domain.BallotValue.DISAGREE then 1L else 0L end), 0L),
  coalesce(sum(case when b.ballotValue = com.notfound.lpickbackend.wiki.command.application.domain.BallotValue.ABSTAIN  then 1L else 0L end), 0L),
  count(b)
    )
    from Ballot b
    where b.debate.dtId = :dtId
    """)
    BallotCount countByDebateGrouped(@Param("dtId") String dtId);
}
