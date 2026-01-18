package com.roberto.mini_authorizer.domain.model;

import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Card {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal INITIAL_BALANCE =
            new BigDecimal("500.00").setScale(MONEY_SCALE, ROUNDING_MODE);

    private Long id;
    private String cardNumber;
    private String password;
    private BigDecimal balance;

    public Card(Long id, String cardNumber, String password, BigDecimal balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = balance.setScale(MONEY_SCALE, ROUNDING_MODE);
    }

    public Card(String cardNumber, String password, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = balance.setScale(MONEY_SCALE, ROUNDING_MODE);
    }


    public static Card create(String cardNumber, String password) {
        validateCardNumber(cardNumber);
        validatePassword(password);
        return new Card(cardNumber, password, INITIAL_BALANCE);
    }

    public void debit(BigDecimal amount) {
        validateDebitAmount(amount);

        BigDecimal normalizedAmount =
                amount.setScale(MONEY_SCALE, ROUNDING_MODE);

        BigDecimal newBalance = this.balance.subtract(normalizedAmount)
                .setScale(MONEY_SCALE, ROUNDING_MODE);

        Optional.of(newBalance)
                .filter(b -> b.compareTo(BigDecimal.ZERO) >= 0)
                .orElseThrow(InsufficientBalanceException::new);

        this.balance = newBalance;
    }


    public boolean passwordMatches(String providedPassword) {
        return Objects.nonNull(providedPassword) && this.password.equals(providedPassword);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    private static void validateCardNumber(String cardNumber) {
        Optional.ofNullable(cardNumber)
                .filter(n -> !n.isBlank())
                .filter(n -> n.matches("\\d{16}"))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid card number. It must contain exactly 16 numeric digits"
                ));
    }

    private static void validatePassword(String password) {
        Optional.ofNullable(password)
                .filter(p -> !p.isBlank())
                .filter(p -> p.length() >= 4)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid password. It must contain at least 4 characters"
                ));
    }

    private void validateDebitAmount(BigDecimal amount) {
        Optional.ofNullable(amount)
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Debit amount must be greater than zero"
                ));
    }

    @Override
    public boolean equals(Object o) {
        return Optional.ofNullable(o)
                .filter(obj -> obj instanceof Card)
                .map(obj -> (Card) obj)
                .filter(other -> Objects.equals(cardNumber, other.cardNumber))
                .isPresent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }
}