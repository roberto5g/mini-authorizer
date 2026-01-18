package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.exceptions.InvalidPasswordException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrectPasswordValidatorTest {

    @Mock
    PasswordEncoder passwordEncoder;

    CorrectPasswordValidator validator;

    Card card;
    Transaction transaction;

    @BeforeEach
    void setup() {
        validator = new CorrectPasswordValidator(passwordEncoder);

        card = new Card(
                "1234567890123456",
                "encoded-password",
                BigDecimal.valueOf(500)
        );

        transaction = new Transaction(
                "1234567890123456",
                "raw-password",
                BigDecimal.valueOf(100)
        );
    }

    @Test
    void shouldValidateWhenPasswordMatches() {
        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                validator.validate(transaction, card)
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> validator.validate(transaction, card)
        );
    }

}