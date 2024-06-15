package br.com.walter.springkeycloak.services.impl;

import br.com.walter.springkeycloak.controllers.dto.UserRequestDTO;
import br.com.walter.springkeycloak.dto.ResponseMessage;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceImpl {

    private Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private RestTemplate restTemplate;


    public ResponseMessage createUser(UserRequestDTO userDTO, String token) {
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

}
