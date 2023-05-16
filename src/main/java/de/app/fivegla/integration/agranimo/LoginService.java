package de.app.fivegla.integration.agranimo;

import com.google.gson.Gson;
import de.app.fivegla.api.Error;
import de.app.fivegla.api.ErrorMessage;
import de.app.fivegla.api.exceptions.BusinessException;
import de.app.fivegla.integration.agranimo.request.LoginRequest;
import de.app.fivegla.integration.agranimo.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Service for login against the API.
 */
@Slf4j
@Service
public class LoginService {

    @Value("${app.sensors.agranimo.url}")
    private String url;

    @Value("${app.sensors.agranimo.username}")
    private String username;

    @Value("${app.sensors.agranimo.password}")
    private String password;

    private final Gson gson;

    public LoginService(Gson gson) {
        this.gson = gson;
    }

    /**
     * Login against the API.
     */
    public Optional<String> login() {
        try {
            var httpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new LoginRequest(username, password))))
                    .header("Content-Type", "application/json")
                    .uri(URI.create(url + "/auth/login"))
                    .build();
            var httpClient = HttpClient.newHttpClient();
            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.CREATED.value()) {
                log.error("Error while login against the API. Status code: {}", response.statusCode());
                throw new BusinessException(ErrorMessage.builder()
                        .error(Error.COULD_NOT_LOGIN_AGAINST_API)
                        .message("Could not login against the API.")
                        .build());
            } else {
                log.info("Successfully logged in against the API.");
                var body = response.body();
                var loginResponse = gson.fromJson(body, LoginResponse.class);
                log.info("Access token found after successful: {}", loginResponse.getAccessToken());
                return Optional.of(loginResponse.getAccessToken());
            }
        } catch (Exception e) {
            log.error("Error while login against the API.", e);
            throw new BusinessException(ErrorMessage.builder()
                    .error(Error.COULD_NOT_LOGIN_AGAINST_API)
                    .message("Could not login against the API.")
                    .build());
        }
    }

}
