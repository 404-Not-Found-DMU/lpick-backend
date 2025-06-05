package com.notfound.lpickbackend.userinfo.query.repository;


import com.notfound.lpickbackend.AUTO_ENTITIES.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTierRepository extends JpaRepository<Tier, String> {

}
