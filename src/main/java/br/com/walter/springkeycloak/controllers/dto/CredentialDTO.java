package br.com.walter.springkeycloak.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CredentialDTO {
    private boolean temporary;
    private String type;
    private String value;
}
