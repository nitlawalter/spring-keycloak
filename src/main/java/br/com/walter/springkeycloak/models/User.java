package br.com.walter.springkeycloak.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Size(min = 6, max = 20, message = "Password should have at least 8 characters and at most 20 characters")
    private String password;

    private String email;
    private String firstName;
    private String lastName;

}
