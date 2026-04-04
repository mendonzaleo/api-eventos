package com.my.events.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventoUpdateDTO {
    private String nome;

    private String localizacao;

    @Future(message = "A data do evento deve ser futura")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataAgendamento;
}
