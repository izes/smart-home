package com.example.smarthome.controller;

import com.example.smarthome.domain.BaseAppliance;
import com.example.smarthome.domain.Fan;
import com.example.smarthome.domain.Light;
import com.example.smarthome.domain.AirConditioner;
import com.example.smarthome.service.ApplianceService;
import com.example.smarthome.service.EventService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appliances")
@RequiredArgsConstructor
public class ApplianceController {
    private final ApplianceService applianceService;
    private final EventService eventService;
    private static final Logger log = LoggerFactory.getLogger(ApplianceController.class);

    @GetMapping
    public ResponseEntity<List<BaseAppliance>> getAllAppliances() {
        return ResponseEntity.ok(applianceService.getAllAppliances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseAppliance> getApplianceById(@PathVariable String id) {
        return applianceService.getApplianceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BaseAppliance> createAppliance(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        if (type == null) {
            throw new IllegalArgumentException("Appliance type is required");
        }

        BaseAppliance appliance;
        switch (type.toLowerCase()) {
            case "light":
                appliance = new Light();
                break;
            case "fan":
                appliance = new Fan();
                break;
            case "airconditioner":
                appliance = new AirConditioner();
                break;
            default:
                throw new IllegalArgumentException("Invalid appliance type: " + type);
        }

        appliance.setName((String) request.get("name"));
        Boolean isOn = (Boolean) request.get("isOn");
        if (isOn != null && isOn) {
            appliance.turnOn();
        } else {
            appliance.turnOff();
        }

        return ResponseEntity.ok(applianceService.saveAppliance(appliance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseAppliance> updateAppliance(@PathVariable String id,
            @RequestBody BaseAppliance appliance) {
        return ResponseEntity.ok(applianceService.updateAppliance(id, appliance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppliance(@PathVariable String id) {
        applianceService.deleteAppliance(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetAllAppliances() {
        log.info("Received reset event, turning off all appliances");
        applianceService.resetAllAppliances();
        return ResponseEntity.ok().build();
    }

}