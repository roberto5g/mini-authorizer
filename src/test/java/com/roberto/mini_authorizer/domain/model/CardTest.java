package com.roberto.mini_authorizer.domain.model;

import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void shouldCreateCardWithInitialBalance() {
        Card card = Card.create("1234567890123456", "1234");

        assertNotNull(card);
        assertEquals("1234567890123456", card.getCardNumber());
        assertEquals("1234", card.getPassword());
        assertEquals(new BigDecimal("500.00"), card.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsInvalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Card.create("123", "1234")
        );

        assertEquals(
                "Invalid card number. It must contain exactly 16 numeric digits",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Card.create("1234567890123456", "12")
        );

        assertEquals(
                "Invalid password. It must contain at least 4 characters",
                exception.getMessage()
        );
    }

    @Test
    void shouldDebitBalanceSuccessfully() {
        Card card = Card.create("1234567890123456", "1234");

        card.debit(BigDecimal.valueOf(100));

        assertEquals(new BigDecimal("400.00"), card.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        Card card = Card.create("1234567890123456", "1234");

        assertThrows(
                InsufficientBalanceException.class,
                () -> card.debit(BigDecimal.valueOf(600))
        );
    }

    @Test
    void shouldThrowExceptionWhenDebitAmountIsZeroOrNegative() {
        Card card = Card.create("1234567890123456", "1234");

        assertThrows(
                IllegalArgumentException.class,
                () -> card.debit(BigDecimal.ZERO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> card.debit(BigDecimal.valueOf(-10))
        );
    }

    @Test
    void shouldMatchPasswordCorrectly() {
        Card card = Card.create("1234567890123456", "1234");

        assertTrue(card.passwordMatches("1234"));
        assertFalse(card.passwordMatches("0000"));
        assertFalse(card.passwordMatches(null));
    }

    @Test
    void shouldValidateSufficientBalance() {
        Card card = Card.create("1234567890123456", "1234");

        assertTrue(card.hasSufficientBalance(BigDecimal.valueOf(100)));
        assertFalse(card.hasSufficientBalance(BigDecimal.valueOf(600)));
    }

    @Test
    void shouldBeEqualWhenCardNumberIsSame() {
        Card card1 = Card.create("1234567890123456", "1234");
        Card card2 = Card.create("1234567890123456", "9999");

        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
    }
}
