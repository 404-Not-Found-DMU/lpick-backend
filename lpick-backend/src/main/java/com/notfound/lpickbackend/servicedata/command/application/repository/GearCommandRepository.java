package com.notfound.lpickbackend.servicedata.command.application.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GearCommandRepository extends JpaRepository<Gear, String> {
}
