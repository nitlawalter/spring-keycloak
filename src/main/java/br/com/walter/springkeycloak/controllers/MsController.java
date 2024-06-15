package br.com.walter.springkeycloak.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class MsController {

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'ADMIN_WRITE')")
    public String adminAccess(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("preferred_username");
        String email = principal.getClaim("email");
        String name = principal.getClaim("name");
        String roles = principal.getClaim("realm_access").toString();
        List<String> listRoles = principal.getClaimAsStringList("realm_access");
        String roles2 = principal.getClaimAsMap("realm_access").get("roles").toString();

        if(hasRole("ADMIN_READ", principal)) {
            return "Acesso concedido ao usuário admin! Info do User ==> " +
                String.format("\nUsername: %s\nEmail: %s\nName: %s\nRoles: %s\nList Roles: %s\n Roles2: %s", username, email, name, roles, listRoles, roles2);
        }else {
            return "Acesso negado ao usuário admin! Info do User ==> " +
                String.format("\nUsername: %s\nEmail: %s\nName: %s\nRoles: %s\nList Roles: %s\n Roles2: %s", username, email, name, roles, listRoles, roles2);
        }

    }

    private boolean hasRole(String role, Jwt principal) {
        List<String> roles = (List<String>) principal.getClaimAsMap("realm_access").get("roles");
        return roles != null && roles.contains(role);
    }

    @GetMapping("/operation")
    @PreAuthorize("hasAnyAuthority('OPERATION_READ', 'OPERATION_WRITE')")
    public String operationAccess() {
        return "Acesso concedido ao usuário operation";
    }

    @GetMapping("/userinfo")
    public String userInfo(@AuthenticationPrincipal Jwt principal) {
        String email = principal.getClaimAsString("email");
        String name = principal.getClaimAsString("name");
        List<String> roles = getRoles(principal);
        return String.format("User info - Name: %s, Email: %s, Roles: %s", name, email, roles);
    }

    private List<String> getRoles(Jwt principal) {
        return (List<String>) principal.getClaimAsMap("realm_access").get("roles");
    }

}
