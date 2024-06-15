package br.com.walter.springkeycloak.services;

import br.com.walter.springkeycloak.models.User;
import org.springframework.http.ResponseEntity;

public interface LoginService<T> {

    ResponseEntity<T> login(User user);

    ResponseEntity<T> logout(User user);

    ResponseEntity<T> refreshToken(String refreshToken);
}
