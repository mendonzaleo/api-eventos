package com.my.events.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioRequestDTO {
    @NotBlank(message = "O nome do usuário é obrigatório")
    private String name;

    @NotBlank(message = "O username é obrigatório")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    @NotEmpty(message = "O usuário deve possuir ao menos um perfil")
    private List<String> roles;
}
