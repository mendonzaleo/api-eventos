package com.my.events.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EventoDTO {
    @NotBlank(message = "Nome do evento é obrigatório")
    private String nome;

    @NotBlank(message = "Local do evento é obrigatório")
    private String localizacao;

    @NotNull(message = "A data do evento é obrigatória")
    @Future(message = "A data do evento deve ser futura")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataAgendamento;
}
