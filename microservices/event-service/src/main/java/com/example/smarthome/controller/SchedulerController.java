package com.example.smarthome.controller;

import com.example.smarthome.scheduler.SystemUpdateScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
    private final SystemUpdateScheduler scheduler;

    @GetMapping("/info")
    public Map<String, String> getSchedulerInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("nextUpdateTime", scheduler.getNextUpdateTime());
        info.put("cronExpression", scheduler.getCronExpression());
        return info;
    }
}