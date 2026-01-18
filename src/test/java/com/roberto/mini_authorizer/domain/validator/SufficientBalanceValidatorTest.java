package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SufficientBalanceValidatorTest {

    @Test
    void shouldValidateWhenBalanceIsSufficient() {
        Card card = new Card(
                "1234567890123456",
                "encoded-password",
                BigDecimal.valueOf(500)
        );

        Transaction transaction = new Transaction(
                "1234567890123456",
                "password",
                BigDecimal.valueOf(100)
        );

        SufficientBalanceValidator validator = new SufficientBalanceValidator();

        assertDoesNotThrow(() ->
                validator.validate(transaction, card)
        );
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        Card card = new Card(
                "1234567890123456",
                "encoded-password",
                BigDecimal.valueOf(50)
        );

        Transaction transaction = new Transaction(
                "1234567890123456",
                "password",
                BigDecimal.valueOf(100)
        );

        SufficientBalanceValidator validator = new SufficientBalanceValidator();

        assertThrows(
                InsufficientBalanceException.class,
                () -> validator.validate(transaction, card)
        );
    }

    @Test
    void shouldValidateWhenBalanceIsExactlyEqualToAmount() {
        Card card = new Card(
                "1234567890123456",
                "encoded-password",
                BigDecimal.valueOf(100)
        );

        Transaction transaction = new Transaction(
                "1234567890123456",
                "password",
                BigDecimal.valueOf(100)
        );

        SufficientBalanceValidator validator = new SufficientBalanceValidator();

        assertDoesNotThrow(() ->
                validator.validate(transaction, card)
        );
    }

}