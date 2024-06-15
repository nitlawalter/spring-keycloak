package br.com.walter.springkeycloak.services;

import br.com.walter.springkeycloak.controllers.dto.UserDTO;

public interface UserService {

    public String createUser(UserDTO userDTO, String token);

}
