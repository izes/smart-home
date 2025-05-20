package com.example.smarthome.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EventService {
    private final RestTemplate restTemplate;
    private final String eventServiceUrl;

    public EventService(@Value("${event.service.url:http://localhost:8081}") String eventServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.eventServiceUrl = eventServiceUrl;
    }

    public void triggerSystemReset() {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "RESET");
            event.put("timestamp", System.currentTimeMillis());
            event.put("message", "Annual system reset triggered");

            log.info("Triggering annual system reset event: {}", event);
            restTemplate.postForEntity(eventServiceUrl + "/api/v1/events/reset", event, Void.class);
            log.info("Annual system reset event triggered successfully");
        } catch (Exception e) {
            log.error("Error triggering annual system reset event", e);
        }
    }
}