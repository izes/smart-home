package com.example.smarthome.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class SseEmitterService {
    private static final Logger logger = LoggerFactory.getLogger(SseEmitterService.class);
    private final Sinks.Many<String> eventSink;
    private final Sinks.Many<String> resetSink;

    public SseEmitterService() {
        this.eventSink = Sinks.many().replay().latest();
        this.resetSink = Sinks.many().multicast().onBackpressureBuffer();
        logger.info("SseEmitterService initialized");
    }

    public void emitEvent(String event) {
        logger.info("Emitting event: {}", event);
        eventSink.tryEmitNext(event);
    }

    public void emitResetEvent() {
        logger.info("Emitting reset event");
        resetSink.tryEmitNext("{\"type\":\"RESET\",\"message\":\"System reset triggered\"}");
    }

    public Flux<String> getEventStream() {
        logger.info("New event stream requested");
        return eventSink.asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnError(error -> logger.error("Error in event stream: {}", error.getMessage()));
    }

    public Flux<String> getResetEventStream() {
        logger.info("New reset event stream requested");
        return resetSink.asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnError(error -> logger.error("Error in reset event stream: {}", error.getMessage()));
    }
}