package com.my.events.DTO;

import com.my.events.model.Usuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponse {
    private String mensagem;
    private Usuario usuario;

    public UsuarioResponse(String mensagem, Usuario usuario) {
        this.mensagem = mensagem;
        this.usuario = usuario;
    }
}
