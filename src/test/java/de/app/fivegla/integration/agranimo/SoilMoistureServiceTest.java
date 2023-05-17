package de.app.fivegla.integration.agranimo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class SoilMoistureServiceTest {

    @Autowired
    private SoilMoistureService soilMoistureService;

    @Test
    void givenInvalidTimePeriodWhenFetchingWaterVolumeShouldNotCauseAnError() {
        soilMoistureService.fetchWaterVolume(Instant.now(), Instant.now().minus(365, ChronoUnit.DAYS));
    }

    @Test
    void givenValidCredentialsWhenFetchingWaterVolumeThenThereShouldBeEntriesForTheZone() {
        soilMoistureService.fetchWaterVolume(Instant.now().minus(365, ChronoUnit.DAYS), Instant.now());
    }

    @Test
    void givenValidCredentialsWhenFetchingWaterHeightThenThereShouldBeEntriesForTheZone() {
        soilMoistureService.fetchWaterHeight(Instant.now().minus(365, ChronoUnit.DAYS), Instant.now());
    }

    @Test
    void givenValidCredentialsWhenFetchingWaterContentThenThereShouldBeEntriesForTheZone() {
        soilMoistureService.fetchWaterContent(Instant.now().minus(365, ChronoUnit.DAYS), Instant.now());
    }
}