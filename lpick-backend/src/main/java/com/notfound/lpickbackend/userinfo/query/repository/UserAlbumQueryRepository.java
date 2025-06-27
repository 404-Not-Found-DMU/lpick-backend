package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserAlbum;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAlbumQueryRepository extends JpaRepository<UserAlbum, String> {

    @Query()
    //    @EntityGraph(attributePaths = {"album"})
    public Page<UserAlbumOwnedResponse> findAllByOauth_OauthId(String oauthId, Pageable pageable);


}
