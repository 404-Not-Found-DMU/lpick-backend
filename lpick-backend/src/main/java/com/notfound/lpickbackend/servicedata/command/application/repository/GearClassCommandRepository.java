package com.notfound.lpickbackend.servicedata.command.application.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.GearClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GearClassCommandRepository extends JpaRepository<GearClass, String> {

}
