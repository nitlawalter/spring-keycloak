package br.com.walter.springkeycloak.services.impl;

import br.com.walter.springkeycloak.components.HttpComponent;
import br.com.walter.springkeycloak.models.User;
import br.com.walter.springkeycloak.services.LoginService;
import br.com.walter.springkeycloak.utils.HttpParamsMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class LoginServiceKeycloakImpl implements LoginService<String> {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}")
    private String keycloakClientId;

    @Value("${keycloak.credentials.secret}")
    private String keycloakClientSecret;

    @Value("${keycloak.user-login.grant-type}")
    private String keycloakGrantType;

    @Autowired
    private HttpComponent httpComponent;

    @Override
    public ResponseEntity<String> login(User user) {
        httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = HttpParamsMapBuilder.builder()
                .withClientId(keycloakClientId)
                .withClientSecret(keycloakClientSecret)
                .withGrantType(keycloakGrantType)
                .withUsername(user.getUsername())
                .withPassword(user.getPassword())
                .build();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpComponent.httpHeaders());

        try {
            ResponseEntity<String> response = httpComponent.restTemplate().postForEntity(
                    keycloakServerUrl + "/realms/REALM_SPRING_API/protocol/openid-connect/token",
                    request,
                    String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()).toString());
        }
    }

    @Override
    public ResponseEntity<String> logout(User user) {
        return null;
    }

    @Override
    public ResponseEntity<String> refreshToken(String refreshToken) {
        httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = HttpParamsMapBuilder.builder()
                .withClientId(keycloakClientId)
                .withClientSecret(keycloakClientSecret)
                .withGrantType("refresh_token")
                .withRefreshToken(refreshToken)
                .build();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpComponent.httpHeaders());

        try {
            ResponseEntity<String> response = httpComponent.restTemplate().postForEntity(
                    keycloakServerUrl + "/protocol/openid-connect/token",
                    request,
                    String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()).toString());
        }

    }
}
