package com.notfound.lpickbackend.userinfo.command.repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoCommandRepository extends JpaRepository<UserInfo, String> {
}
