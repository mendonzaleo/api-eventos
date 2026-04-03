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
    private String nome;

    @NotBlank(message = "O username é obrigatório")
    private String sobrenome;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @NotEmpty(message = "O usuário deve possuir ao menos um perfil")
    private List<String> perfil;
}
