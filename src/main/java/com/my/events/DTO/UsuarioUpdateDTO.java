package com.my.events.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioUpdateDTO {
    private String nome;
    private String username;
    private String senha;
    private List<String> perfis;
}
