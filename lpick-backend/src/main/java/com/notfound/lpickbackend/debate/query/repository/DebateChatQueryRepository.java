package com.notfound.lpickbackend.debate.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.DebateChat;
import com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebateChatQueryRepository extends JpaRepository<DebateChat, String> {

    int countByOauth_OauthId(String oauthId);

    @Query("""
            select new com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo(
              dc.dscId,
              o.oauthId,
              o.nickname,
              dc.content,
              dc.createdAt,
              dc.isBlind,
              p.dscId,
              dc.oauth.profile
            )
            from DebateChat dc
            join dc.oauth o
            left join dc.parentDebateChat p
            where dc.dt.dtId = :dtId
            order by dc.createdAt asc
            """)
    List<DebateChatInfo> findAllByDtIdOrderByCreatedAtDesc(String dtId);
}
