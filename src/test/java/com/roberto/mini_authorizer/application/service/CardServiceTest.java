package com.roberto.mini_authorizer.application.service;

import com.roberto.mini_authorizer.domain.exceptions.CardAlreadyExistsException;
import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepositoryPort cardRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CardService cardService;

    private final String cardNumber = "1234567890123456";
    private final String password = "1234";
    private final String encodedPassword = "encoded-password";

    @Test
    void shouldCreateCardSuccessfully() {
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Card savedCard = Card.create(cardNumber, encodedPassword);

        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        Card result = cardService.createCard(cardNumber, password);

        assertNotNull(result);
        assertEquals(cardNumber, result.getCardNumber());
        assertEquals(encodedPassword, result.getPassword());

        verify(passwordEncoder).encode(password);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenCardAlreadyExists() {
        when(passwordEncoder.encode(password))
                .thenReturn("encoded-password");

        when(cardRepository.save(any(Card.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThrows(
                CardAlreadyExistsException.class,
                () -> cardService.createCard(cardNumber, password)
        );

        verify(passwordEncoder).encode(password);
        verify(cardRepository).save(any(Card.class));
    }


    @Test
    void shouldReturnBalanceWhenCardExists() {
        Card card = Card.create(cardNumber, encodedPassword);

        when(cardRepository.findByCardNumber(cardNumber))
                .thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBalance(cardNumber);

        assertEquals(0, result.compareTo(BigDecimal.valueOf(500)));
        verify(cardRepository).findByCardNumber(cardNumber);
    }


    @Test
    void shouldThrowExceptionWhenCardDoesNotExist() {
        when(cardRepository.findByCardNumber(cardNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                CardNotFoundException.class,
                () -> cardService.getBalance(cardNumber)
        );

        verify(cardRepository).findByCardNumber(cardNumber);
    }
}
