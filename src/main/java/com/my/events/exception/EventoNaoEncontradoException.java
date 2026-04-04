package com.my.events.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventoNaoEncontradoException extends RuntimeException{
    public EventoNaoEncontradoException() {
        super("Evento não encontrado");
    }

    public EventoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public EventoNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
