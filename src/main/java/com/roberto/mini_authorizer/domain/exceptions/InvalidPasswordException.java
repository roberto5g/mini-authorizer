package com.roberto.mini_authorizer.domain.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("SENHA_INVALIDA");
    }
}
