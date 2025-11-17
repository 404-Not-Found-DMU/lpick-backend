package com.notfound.lpickbackend.servicedata.command.application.repository;

import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GearDefaultSettingRepository extends JpaRepository<Gear, Long> {
    Optional<Gear> findByBrandAndModelName(String brand, String modelName);
}