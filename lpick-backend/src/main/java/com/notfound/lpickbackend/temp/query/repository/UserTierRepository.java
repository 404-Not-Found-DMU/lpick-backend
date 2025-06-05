package com.notfound.lpickbackend.temp.query.repository;


import com.notfound.lpickbackend.AUTO_ENTITIES.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTierRepository extends JpaRepository<Tier, String> {

}
