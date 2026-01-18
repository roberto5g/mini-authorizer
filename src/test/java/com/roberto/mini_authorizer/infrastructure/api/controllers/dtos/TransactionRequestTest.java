package com.roberto.mini_authorizer.infrastructure.api.controllers.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValidWhenAllFieldsAreCorrect() {
        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(10.50)
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenCardNumberIsBlank() {
        var request = new TransactionRequest(
                "",
                "1234",
                BigDecimal.valueOf(10)
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("cardNumber"));
    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {
        var request = new TransactionRequest(
                "1234567890123456",
                "",
                BigDecimal.valueOf(10)
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("cardPassword"));
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                null
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("amount"));
    }

    @Test
    void shouldFailWhenAmountIsZero() {
        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                BigDecimal.ZERO
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("amount"));
    }

    @Test
    void shouldFailWhenAmountIsNegative() {
        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(-1)
        );

        Set<ConstraintViolation<TransactionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("amount"));
    }
}
