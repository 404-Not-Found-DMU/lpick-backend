package com.notfound.lpickbackend.temp.query.repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoQueryRepository extends JpaRepository<UserInfo, String> {
}
