package com.my.events.exception;

public class UsuarioNaoEncontradoException extends RuntimeException{
    public UsuarioNaoEncontradoException() {
        super();
    }

    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
