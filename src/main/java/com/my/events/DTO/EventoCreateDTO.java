package com.my.events.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventoCreateDTO {

    @NotBlank(message = "O nome é obrigatório!")
    private String nome;
    @NotBlank(message = "A localização é obrigatória!")
    private String localizacao;
    @NotNull(message = "A data de agendamento é obrigatória!")
    @Future(message = "A data do evento deve ser futura!")
    private LocalDate dataAgendamento;


}
