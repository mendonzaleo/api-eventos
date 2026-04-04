package com.my.events.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {

    @NotBlank
    private Integer id;

    @NotBlank(message = "O nome do usuário é obrigatório")
    private String nome;

    @NotBlank(message = "O username é obrigatório")
    private String username;

    public UsuarioDTO() {}

    public UsuarioDTO(Integer id, String nome, String username) {
        this.id = id;
        this.nome = nome;
        this.username = username;
    }
}
