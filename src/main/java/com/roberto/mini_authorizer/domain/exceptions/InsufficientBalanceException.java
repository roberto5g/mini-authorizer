package com.roberto.mini_authorizer.domain.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("SALDO_INSUFICIENTE");
    }
}
