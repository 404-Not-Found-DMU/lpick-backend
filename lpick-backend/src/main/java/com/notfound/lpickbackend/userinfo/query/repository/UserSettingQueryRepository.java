package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingQueryRepository extends JpaRepository<UserSetting, String> {
}
