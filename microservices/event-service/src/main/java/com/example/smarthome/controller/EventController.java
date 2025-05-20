package com.example.smarthome.controller;

import com.example.smarthome.service.SseEmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final SseEmitterService emitterService;

    public EventController(SseEmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @GetMapping(value = "/reset", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResetEvents() {
        return emitterService.getResetEventStream();
    }
}