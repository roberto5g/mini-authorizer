package com.roberto.mini_authorizer.infrastructure.api.controllers.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CardRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValidWhenCardNumberAndPasswordAreCorrect() {
        var request = new CardRequest(
                "1234567890123456",
                "1234"
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenCardNumberIsBlank() {
        var request = new CardRequest(
                "",
                "1234"
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("cardNumber"));
    }

    @Test
    void shouldFailWhenCardNumberHasLessThan16Digits() {
        var request = new CardRequest(
                "123",
                "1234"
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("cardNumber"));
    }

    @Test
    void shouldFailWhenCardNumberContainsLetters() {
        var request = new CardRequest(
                "1234abcd90123456",
                "1234"
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("cardNumber"));
    }

    @Test
    void shouldFailWhenPasswordIsBlank() {
        var request = new CardRequest(
                "1234567890123456",
                ""
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("password"));
    }

    @Test
    void shouldFailWhenPasswordHasLessThanFourCharacters() {
        var request = new CardRequest(
                "1234567890123456",
                "123"
        );

        Set<ConstraintViolation<CardRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("password"));
    }
}
