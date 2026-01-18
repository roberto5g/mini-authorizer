package com.roberto.mini_authorizer.ports.out;

import com.roberto.mini_authorizer.domain.model.Card;

import java.util.Optional;

public interface CardRepositoryPort {

    Card save(Card card);

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByCardNumberWithLock(String cardNumber);

    boolean existsByCardNumber(String cardNumber);
}
