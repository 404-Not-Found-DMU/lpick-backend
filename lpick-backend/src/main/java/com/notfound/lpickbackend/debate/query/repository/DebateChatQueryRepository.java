package com.notfound.lpickbackend.debate.query.repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.DebateChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateChatQueryRepository extends JpaRepository<DebateChat, String> {

    int countByOauth_OauthId(String oauthId);
}
