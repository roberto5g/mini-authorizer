package com.roberto.mini_authorizer.application.service;

import com.roberto.mini_authorizer.domain.exceptions.CardAlreadyExistsException;
import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.ports.in.CardServicePort;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardService implements CardServicePort {

    private final CardRepositoryPort cardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Card createCard(String cardNumber, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Card newCard = Card.create(cardNumber, encodedPassword);

        try {
            return cardRepository.save(newCard);
        } catch (DataIntegrityViolationException ex) {
            throw new CardAlreadyExistsException(cardNumber, password);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(Card::getBalance)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }
}
