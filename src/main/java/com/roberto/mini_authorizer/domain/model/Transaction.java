package com.roberto.mini_authorizer.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Transaction {
    private final String cardNumber;
    private final String cardPassword;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(String cardNumber, String cardPassword, BigDecimal amount) {
        this.cardNumber = validateCardNumber(cardNumber);
        this.cardPassword = validatePassword(cardPassword);
        this.amount = validateAmount(amount);
        this.timestamp = LocalDateTime.now();
    }

    private String validateCardNumber(String cardNumber) {
        return Optional.ofNullable(cardNumber)
                .filter(n -> !n.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Card number must not be empty"));
    }

    private String validatePassword(String password) {
        return Optional.ofNullable(password)
                .filter(p -> !p.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Password must not be empty"));
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        return Optional.ofNullable(amount)
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .orElseThrow(() -> new IllegalArgumentException("Amount must be greater than zero"));
    }

    @Override
    public boolean equals(Object o) {
        return Optional.ofNullable(o)
                .filter(obj -> obj instanceof Transaction)
                .map(obj -> (Transaction) obj)
                .filter(other -> Objects.equals(cardNumber, other.cardNumber))
                .isPresent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }
}
