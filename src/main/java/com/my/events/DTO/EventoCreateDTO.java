package com.my.events.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventoCreateDTO {

    private String nome;
    private String localizacao;
    private LocalDate dataAgendamento;


}
