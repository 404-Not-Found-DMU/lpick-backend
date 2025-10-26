package com.notfound.lpickbackend.debate.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.query.dto.response.DebateHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebateQueryRepository extends JpaRepository<Debate, String> {

    @Query("""
select new com.notfound.lpickbackend.wiki.query.dto.response.DebateHeader(
  d.dtId,
  d.debateName,
  d.debateSubject,
  o.nickname, 
  d.createdAt, 
  coalesce(
     (select max(dc2.createdAt)
        from DebateChat dc2
       where dc2.dt.dtId = d.dtId),
     d.createdAt
  ),
  (select count(dc) from DebateChat dc
     where dc.dt.dtId = d.dtId),
  d.isEnd
)
from Debate d
join d.oauth o
where d.wiki.wikiId = :wikiId
order by
  case
    when d.isEnd = com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus.OPEN  then 0
    when d.isEnd = com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus.VOTE  then 1
    when d.isEnd = com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus.CLOSE then 2
    else 99
  end asc,
  coalesce(
     (select max(dc3.createdAt) from DebateChat dc3 where dc3.dt.dtId = d.dtId),
     d.createdAt
  ) asc
""")
    List<DebateHeader> findAllDebateHeaderByWikiId(String wikiId);
}
