package br.com.walter.springkeycloak.controllers;

import br.com.walter.springkeycloak.controllers.dto.UserRequestDTO;
import br.com.walter.springkeycloak.dto.ResponseMessage;
import br.com.walter.springkeycloak.services.UserService;
import br.com.walter.springkeycloak.services.impl.UserServiceImpl;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody UserRequestDTO userDTO, @RequestParam String token) {
        ResponseMessage res = userService.createUser(userDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
