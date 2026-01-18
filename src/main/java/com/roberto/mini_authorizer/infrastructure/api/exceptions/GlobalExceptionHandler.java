package com.roberto.mini_authorizer.infrastructure.api.exceptions;

import com.roberto.mini_authorizer.domain.exceptions.*;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardResponse;
import com.roberto.mini_authorizer.infrastructure.persistence.mapper.CardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<CardResponse> handleCardAlreadyExists(CardAlreadyExistsException ex) {
        log.warn("Attempt to create duplicated card: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(CardMapper.toResponse(ex.getCardNumber(), ex.getPassword()));
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Void> handleCardNotFound(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(CardNotFoundForTransactionException.class)
    public ResponseEntity<String> handleCardNotFoundForTransaction(CardNotFoundForTransactionException ex) {
        log.warn("Card not found for transaction: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ex.getMessage());
    }

    @ExceptionHandler({
            InsufficientBalanceException.class,
            InvalidPasswordException.class
    })
    public ResponseEntity<String> handleTransactionRuleViolation(RuntimeException ex) {
        log.info("Transaction denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation error: {}", errors);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

}
