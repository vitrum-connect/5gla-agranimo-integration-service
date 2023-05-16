package de.app.fivegla.integration.agranimo.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response for login against the API.
 */
@Getter
@Setter
public class LoginResponse {

    private String id;
    private String username;
    private String email;
    private String role;
    private String accessToken;

}
