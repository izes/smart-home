package com.example.smarthome.repository;

import com.example.smarthome.domain.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FanRepository extends JpaRepository<Fan, String> {
}