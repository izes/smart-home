package com.example.smarthome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeviceManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeviceManagementApplication.class, args);
    }
}