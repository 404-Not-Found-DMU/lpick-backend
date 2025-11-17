package com.notfound.lpickbackend.userinfo.command.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGearCommandRepository extends JpaRepository<UserGear, String> {
    boolean existsByEq_GearIdAndOauth_OauthId(String gearId, String oAuthId);
}
