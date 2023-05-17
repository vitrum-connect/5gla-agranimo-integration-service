package de.app.fivegla.integration.agranimo;

import com.google.gson.Gson;
import de.app.fivegla.integration.agranimo.domain.SoilMoistureType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

/**
 * Service for integration with Agranimo.
 */
@Slf4j
@Service
public class SoilMoistureService {

    @Value("${app.sensors.agranimo.url}")
    private String url;

    private final Gson gson;
    private final ZoneService zoneService;
    private final LoginService loginService;

    public SoilMoistureService(Gson gson, ZoneService zoneService, LoginService loginService) {
        this.gson = gson;
        this.zoneService = zoneService;
        this.loginService = loginService;
    }

    /**
     * Fetch the soil moisture from the API.
     */
    public void fetchData(Instant dateStart, Instant dateEnd, SoilMoistureType type) {
        zoneService.fetchZones().forEach(zone -> {
            log.info("Fetching soil moisture data for zone {}.", zone.getName());
            try {
                var httpRequest = HttpRequest.newBuilder()
                        .GET()
                        .header("Authorization", "Bearer " + loginService.fetchAccessToken())
                        .uri(URI.create(url + String.format("/zone/%s/data/soilmoisture", zone.getId())))
                        .build();
                var httpClient = HttpClient.newHttpClient();
                var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.value()) {
                    log.error("Error while fetching soil moisture from the API. Status code: {}", response.statusCode());
                    log.info("Could not fetch soil moisture data for zone {}.", zone.getName());
                } else {
                    log.info("Successfully fetched soil moisture from the API.");
                    log.debug("Response body: {}", response.body());
                    log.info("Successfully fetched soil moisture data for zone {}.", zone.getName());
                }
            } catch (Exception e) {
                log.error("Error while fetching soil moisture from the API.", e);
                log.info("Could not fetch soil moisture data for zone {}.", zone.getName());
            }
        });
    }

}
