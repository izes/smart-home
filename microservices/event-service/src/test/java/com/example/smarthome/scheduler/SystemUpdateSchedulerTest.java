package com.example.smarthome.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import com.example.smarthome.service.SseEmitterService;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SystemUpdateSchedulerTest {

    @Mock
    private SseEmitterService emitterService;

    @Mock
    private RestTemplate restTemplate;

    private SystemUpdateScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new SystemUpdateScheduler(emitterService, restTemplate);
    }

    @Test
    void getCronExpression_ShouldReturnCorrectExpression() {
        // Act
        String cronExpression = scheduler.getCronExpression();

        // Assert
        assertEquals("0 0 1 1 1 *", cronExpression);
    }

    @Test
    void testSchedulerTrigger() {
        // Act
        scheduler.performAnnualSystemUpdate();

        // Assert
        verify(emitterService).emitResetEvent();
    }

    @Test
    public void testPerformAnnualSystemUpdate_asIfJan1At1AM() throws Exception{

        SystemUpdateScheduler scheduler = new SystemUpdateScheduler(emitterService, restTemplate);
        Field urlField = SystemUpdateScheduler.class.getDeclaredField("deviceManagementServiceUrl");
        urlField.setAccessible(true);
        urlField.set(scheduler, "http://device-management-service:8080");

        // Act
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            LocalDateTime fakeNow = LocalDateTime.of(2025, 1, 1, 1, 0);
            mockedTime.when(() -> LocalDateTime.now(ZoneId.systemDefault())).thenReturn(fakeNow);

            scheduler.performAnnualSystemUpdate();

            // Assert
            verify(emitterService, times(1)).emitResetEvent();
            verify(restTemplate, times(1)).postForEntity(
                    eq("http://device-management-service:8080/api/v1/appliances/reset"),
                    isNull(),
                    eq(Void.class)
            );
        }
    }

    @Test
    void testNextUpdateTime() {
        // Act
        String nextUpdateTime = scheduler.getNextUpdateTime();

        // Assert
        assertEquals("01/01/2025 01:00 UTC", nextUpdateTime);
    }
}