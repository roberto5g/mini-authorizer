package com.roberto.mini_authorizer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateTransactionSuccessfully() {
        Transaction transaction = new Transaction(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(100)
        );

        assertNotNull(transaction);
        assertEquals("1234567890123456", transaction.getCardNumber());
        assertEquals("1234", transaction.getCardPassword());
        assertEquals(BigDecimal.valueOf(100), transaction.getAmount());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(null, "1234", BigDecimal.valueOf(100))
        );

        assertEquals("Card number must not be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("   ", "1234", BigDecimal.valueOf(100))
        );

        assertEquals("Card number must not be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("1234567890123456", null, BigDecimal.valueOf(100))
        );

        assertEquals("Password must not be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("1234567890123456", "", BigDecimal.valueOf(100))
        );

        assertEquals("Password must not be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("1234567890123456", "1234", null)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("1234567890123456", "1234", BigDecimal.ZERO)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction("1234567890123456", "1234", BigDecimal.valueOf(-10))
        );
    }

    @Test
    void shouldBeEqualWhenCardNumberIsSame() {
        Transaction t1 = new Transaction(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(100)
        );

        Transaction t2 = new Transaction(
                "1234567890123456",
                "9999",
                BigDecimal.valueOf(200)
        );

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenCardNumberIsDifferent() {
        Transaction t1 = new Transaction(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(100)
        );

        Transaction t2 = new Transaction(
                "9999999999999999",
                "1234",
                BigDecimal.valueOf(100)
        );

        assertNotEquals(t1, t2);
    }
}
