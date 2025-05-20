package com.example.smarthome.scheduler;

import com.example.smarthome.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemUpdateScheduler {
    private final SseEmitterService emitterService;
    private final RestTemplate restTemplate;

    @Value("${device.management.service.url:http://device-management-service:8080}")
    private String deviceManagementServiceUrl;

    private static final String CRON_EXPRESSION = "0 0 1 1 1 *";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Scheduled(cron = CRON_EXPRESSION)
    public void performAnnualSystemUpdate() {
        log.info("Starting annual system update at {}", LocalDateTime.now(ZoneId.systemDefault()));

        try {
            // First, emit the SSE event to the frontend
            emitterService.emitResetEvent();

            // Then, call the reset endpoint on device-management-service
            restTemplate.postForEntity(deviceManagementServiceUrl + "/api/v1/appliances/reset", null, Void.class);

            log.info("Annual system update completed successfully");
        } catch (Exception e) {
            log.error("Error during annual system update", e);
        }
    }

    public String getNextUpdateTime() {
        String[] cronParts = CRON_EXPRESSION.split("\\s+");
        if (cronParts.length < 6) {
            log.error("Invalid cron expression: {}", CRON_EXPRESSION);
            return "Invalid schedule";
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nextUpdate = LocalDateTime.now(ZoneId.systemDefault())
                .withSecond(Integer.parseInt(cronParts[0]))
                .withMinute(Integer.parseInt(cronParts[1]))
                .withHour(Integer.parseInt(cronParts[2]))
                .withDayOfMonth(Integer.parseInt(cronParts[3]))
                .withMonth(Integer.parseInt(cronParts[4]));

        if (now.isAfter(nextUpdate)) {
            nextUpdate = nextUpdate.plusYears(1);
        }

        return nextUpdate.format(TIME_FORMATTER) + " UTC";
    }

    public String getCronExpression() {
        return CRON_EXPRESSION;
    }
}