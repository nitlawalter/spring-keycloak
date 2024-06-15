package br.com.walter.springkeycloak.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserDTO {

    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

}
