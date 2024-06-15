package br.com.walter.springkeycloak.services.impl;

import br.com.walter.springkeycloak.controllers.dto.UserRequestDTO;
import br.com.walter.springkeycloak.dto.ResponseMessage;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl {

    private Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;


    private ResponseMessage createUser(UserRequestDTO userDTO, String token) {
        ResponseMessage message = new ResponseMessage();

        String url = serverUrl + "/admin/realms/" + realm + "/users";

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UserRequestDTO> request = new HttpEntity<>(userDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            message.setMessage("User created successfully");
            return message;
        } else {
            message.setMessage("Error creating user: " + response.getStatusCode());
            return message;
        }

    }

    public ResponseMessage createUserNew(UserRequestDTO userDTO) {
        String token = getToken();
        return createUser(userDTO, token);
    }

    private String getToken() {
        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Map<String, String> params = new HashMap<>();
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "client_credentials");

        //HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            }
        }

        throw new RuntimeException("Failed to retrieve access token");
    }

}
