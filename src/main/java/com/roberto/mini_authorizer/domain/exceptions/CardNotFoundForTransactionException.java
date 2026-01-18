package com.roberto.mini_authorizer.domain.exceptions;

public class CardNotFoundForTransactionException extends RuntimeException {
    public CardNotFoundForTransactionException() {
        super("CARTAO_INEXISTENTE");
    }
}
