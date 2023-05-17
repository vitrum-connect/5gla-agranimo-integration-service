package de.app.fivegla.integration.agranimo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SoilMoistureServiceTest {

    @Autowired
    private SoilMoistureService soilMoistureService;

    @Test
    void givenValidCredentialsWhenLoginThenTheRequestShouldBeAccepted() {
    }
}