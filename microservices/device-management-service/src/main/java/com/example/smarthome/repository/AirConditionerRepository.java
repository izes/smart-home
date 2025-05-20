package com.example.smarthome.repository;

import com.example.smarthome.domain.AirConditioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirConditionerRepository extends JpaRepository<AirConditioner, String> {
}