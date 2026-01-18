package com.roberto.mini_authorizer.application.service;

import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundForTransactionException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import com.roberto.mini_authorizer.domain.validator.ValidatorChain;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @Mock
    private ValidatorChain validatorChain;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private Card card;

    @BeforeEach
    void setup() {
        transaction = new Transaction(
                "1234567812345678",
                "1234",
                BigDecimal.valueOf(100)
        );

        card = new Card(
                1L,
                "1234567812345678",
                "1234",
                BigDecimal.valueOf(500)
        );
    }

    @Test
    void shouldAuthorizeTransactionSuccessfully() {
        when(cardRepository.findByCardNumberWithLock(transaction.getCardNumber()))
                .thenReturn(Optional.of(card));

        transactionService.authorize(transaction);

        verify(validatorChain).validate(transaction, card);
        verify(cardRepository).save(card);

        assertEquals(
                0,
                BigDecimal.valueOf(400).compareTo(card.getBalance())
        );
    }

    @Test
    void shouldThrowExceptionWhenCardDoesNotExist() {
        when(cardRepository.findByCardNumberWithLock(transaction.getCardNumber()))
                .thenReturn(Optional.empty());

        assertThrows(
                CardNotFoundForTransactionException.class,
                () -> transactionService.authorize(transaction)
        );

        verify(cardRepository)
                .findByCardNumberWithLock(transaction.getCardNumber());
        verifyNoInteractions(validatorChain);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveCardWhenValidationFails() {
        when(cardRepository.findByCardNumberWithLock(transaction.getCardNumber()))
                .thenReturn(Optional.of(card));

        doThrow(new RuntimeException("validation error"))
                .when(validatorChain)
                .validate(transaction, card);

        assertThrows(
                RuntimeException.class,
                () -> transactionService.authorize(transaction)
        );

        verify(validatorChain).validate(transaction, card);
        verify(cardRepository, never()).save(any());
    }

}
