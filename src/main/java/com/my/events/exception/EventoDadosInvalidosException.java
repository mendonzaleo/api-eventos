package com.my.events.exception;

public class EventoDadosInvalidosException extends RuntimeException{
    public EventoDadosInvalidosException(String mensagem){
        super(mensagem);
    }
}
