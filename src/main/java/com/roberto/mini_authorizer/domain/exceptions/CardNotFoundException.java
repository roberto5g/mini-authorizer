package com.roberto.mini_authorizer.domain.exceptions;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String cardNumber) {
        super("Card not found: " + cardNumber);
    }
}
