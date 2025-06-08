package com.notfound.lpickbackend.tier.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TierCommandRepository extends JpaRepository<Tier, String> {
}
