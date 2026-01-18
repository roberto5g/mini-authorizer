package com.roberto.mini_authorizer.infrastructure.api.exceptions;

import com.roberto.mini_authorizer.domain.exceptions.CardAlreadyExistsException;
import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundException;
import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import com.roberto.mini_authorizer.domain.exceptions.InvalidPasswordException;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleCardAlreadyExistsException() {
        String cardNumber = "1234567890123456";
        String password = "1234";
        CardAlreadyExistsException exception = new CardAlreadyExistsException(cardNumber, password);

        ResponseEntity<CardResponse> response = handler.handleCardAlreadyExists(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().cardNumber()).isEqualTo(cardNumber);
        assertThat(response.getBody().password()).isEqualTo(password);
    }

    @Test
    void shouldHandleCardNotFoundException() {
        String cardNumber = "1234567890123456";
        CardNotFoundException exception = new CardNotFoundException(cardNumber);

        ResponseEntity<Void> response = handler.handleCardNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldHandleInsufficientBalanceException() {
        InsufficientBalanceException exception = new InsufficientBalanceException();

        ResponseEntity<String> response = handler.handleTransactionRuleViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldHandleInvalidPasswordException() {
        InvalidPasswordException exception = new InvalidPasswordException();

        ResponseEntity<String> response = handler.handleTransactionRuleViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("cardRequest", "cardNumber", "must not be blank");
        FieldError fieldError2 = new FieldError("cardRequest", "password", "size must be between 4 and 6");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter,
                bindingResult
        );

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get("cardNumber")).isEqualTo("must not be blank");
        assertThat(response.getBody().get("password")).isEqualTo("size must be between 4 and 6");
    }

    @Test
    void shouldHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<String> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal server error");
    }

    @Test
    void shouldHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("Null value encountered");

        ResponseEntity<String> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal server error");
    }
}