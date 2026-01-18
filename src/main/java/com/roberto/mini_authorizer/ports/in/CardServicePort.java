package com.roberto.mini_authorizer.ports.in;

import com.roberto.mini_authorizer.domain.model.Card;

import java.math.BigDecimal;

public interface CardServicePort {
    Card createCard(String cardNumber, String password);
    BigDecimal getBalance(String cardNumber);
}
