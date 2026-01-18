package com.roberto.mini_authorizer.domain.exceptions;

import lombok.Getter;

@Getter
public class CardAlreadyExistsException extends RuntimeException {
    private final String cardNumber;
    private final String password;

    public CardAlreadyExistsException(String cardNumber, String password) {
        super();
        this.cardNumber = cardNumber;
        this.password = password;
    }
}
