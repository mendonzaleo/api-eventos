package com.my.events.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tipo;
    private LocalDateTime expiracao;
}
