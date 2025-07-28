package com.notfound.lpickbackend.userinfo.command.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlbumCommandRepository extends JpaRepository<UserAlbum, String> {
}
