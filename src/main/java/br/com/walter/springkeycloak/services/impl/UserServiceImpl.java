package br.com.walter.springkeycloak.services.impl;

import br.com.walter.springkeycloak.controllers.dto.UserDTO;
import br.com.walter.springkeycloak.controllers.dto.UserRequestDTO;
import br.com.walter.springkeycloak.dto.ResponseMessage;
import br.com.walter.springkeycloak.models.User;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl {

    private Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username("adilson")
                .password("123456")
                .clientId("CLIENT_SPRING")
                .resteasyClient(ResteasyClientBuilder.newBuilder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                        .build())
                .build();
    }

    public ResponseMessage createUser2(UserRequestDTO userDTO, String token) {
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

    public Object[] createUser(UserDTO user, String token){
        ResponseMessage message = new ResponseMessage();
        int statusId = 0;
        try {
            UsersResource usersResource = getUsersResource();
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername(user.getUserName());
            userRepresentation.setEmail(user.getEmail());
            userRepresentation.setFirstName(user.getFirstName());
            userRepresentation.setLastName(user.getLastName());
            userRepresentation.setEnabled(true);

            Response result = usersResource.create(userRepresentation);
            statusId = result.getStatus();

            if(statusId == 201){
                String path = result.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);
                CredentialRepresentation passwordCredential = new CredentialRepresentation();
                passwordCredential.setTemporary(false);
                passwordCredential.setType(CredentialRepresentation.PASSWORD);
                passwordCredential.setValue(user.getPassword());
                usersResource.get(userId).resetPassword(passwordCredential);

                RealmResource realmResource = getRealmResource();
                RoleRepresentation roleRepresentation = realmResource.roles().get("realm-user").toRepresentation();
                realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleRepresentation));
                message.setMessage("usuario creado con éxito");
            }else if(statusId == 409){
                message.setMessage("ese usuario ya existe");
            }else{
                message.setMessage("error creando el usuario");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return new Object[]{statusId, message};
    }

    private RealmResource getRealmResource(){
        return keycloak.realm(realm);
    }

    private UsersResource getUsersResource(){
        return getRealmResource().users();
    }


//    @Override
//    public String createUser(UserDTO dto, String token) {
//        // Create credentials
//        CredentialRepresentation credential = new CredentialRepresentation();
//        credential.setTemporary(false);
//        credential.setType(CredentialRepresentation.PASSWORD);
//        //credential.setValue(CredentialRepresentation.PASSWORD);
//        credential.setValue(dto.getPassword());
//
//        // Create user representation
//        UserRepresentation user = new UserRepresentation();
//        user.setUsername(dto.getUserName());
//        user.setEmail(dto.getEmail());
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setEnabled(true);
//        user.setCredentials(Collections.singletonList(credential));
//
//        // Create user in Keycloak
//        Response response = keycloak.realm(realm).users().create(user);
//
//        if (response.getStatus() == 201) {
//            return "User created successfully" + response.getStatusInfo().toString();
//        } else {
//            return "Failed to create user: " + response.getStatusInfo().toString();
//        }
//    }


}
