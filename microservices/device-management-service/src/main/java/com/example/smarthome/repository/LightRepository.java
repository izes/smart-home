package com.example.smarthome.repository;

import com.example.smarthome.domain.Light;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LightRepository extends JpaRepository<Light, String> {
}