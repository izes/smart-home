package com.example.smarthome.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplianceEvent {
    private String applianceId;
    private String applianceType;
    private String eventType; // UPDATE, DELETE, RESET
    private String details;
    private LocalDateTime timestamp;

    public static ApplianceEvent createUpdateEvent(String applianceId, String applianceType, String details) {
        return new ApplianceEvent(applianceId, applianceType, "UPDATE", details, LocalDateTime.now());
    }

    public static ApplianceEvent createDeleteEvent(String applianceId, String applianceType) {
        return new ApplianceEvent(applianceId, applianceType, "DELETE", null, LocalDateTime.now());
    }

    public static ApplianceEvent createResetEvent() {
        return new ApplianceEvent(null, null, "RESET", "System reset initiated", LocalDateTime.now());
    }
}