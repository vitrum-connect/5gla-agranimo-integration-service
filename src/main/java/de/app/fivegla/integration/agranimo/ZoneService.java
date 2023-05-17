package de.app.fivegla.integration.agranimo;

import com.google.gson.Gson;
import de.app.fivegla.api.Error;
import de.app.fivegla.api.ErrorMessage;
import de.app.fivegla.api.exceptions.BusinessException;
import de.app.fivegla.integration.agranimo.cache.UserDataCache;
import de.app.fivegla.integration.agranimo.dto.Zone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Service for login against the API.
 */
@Slf4j
@Service
public class ZoneService {

    @Value("${app.sensors.agranimo.url}")
    private String url;

    @Value("${app.sensors.agranimo.username}")
    private String username;

    @Value("${app.sensors.agranimo.password}")
    private String password;

    private final Gson gson;
    private final LoginService loginService;
    private final UserDataCache userDataCache;

    public ZoneService(Gson gson, LoginService loginService, UserDataCache userDataCache) {
        this.gson = gson;
        this.loginService = loginService;
        this.userDataCache = userDataCache;
    }

    /**
     * Login against the API.
     */
    public List<Zone> fetchZones() {
        if (userDataCache.isExpired()) {
            try {
                var httpRequest = HttpRequest.newBuilder()
                        .GET()
                        .header("Authorization", "Bearer " + loginService.fetchAccessToken())
                        .uri(URI.create(url + "/zone"))
                        .build();
                var httpClient = HttpClient.newHttpClient();
                var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpStatus.OK.value()) {
                    log.error("Error while fetching zones from the API. Status code: {}", response.statusCode());
                    throw new BusinessException(ErrorMessage.builder()
                            .error(Error.COULD_NOT_FETCH_ZONES)
                            .message("Could not fetch zones.")
                            .build());
                } else {
                    log.info("Sucessfully fetched zones from the API.");
                    log.debug("Response body: {}", response.body());
                    var body = response.body();
                    var zones = gson.fromJson(body, Zone[].class);
                    userDataCache.setZones(List.of(zones));
                    return List.of(zones);
                }
            } catch (Exception e) {
                log.error("Error while fetching the zones.", e);
                throw new BusinessException(ErrorMessage.builder()
                        .error(Error.COULD_NOT_FETCH_ZONES)
                        .message("Could not fetch zones.")
                        .build());
            }
        } else {
            return userDataCache.getZones();
        }
    }

}
