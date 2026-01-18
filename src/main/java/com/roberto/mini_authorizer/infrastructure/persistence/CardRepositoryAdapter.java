package com.roberto.mini_authorizer.infrastructure.persistence;

import com.roberto.mini_authorizer.infrastructure.persistence.repository.CardJpaRepository;
import com.roberto.mini_authorizer.infrastructure.persistence.entity.CardEntity;
import com.roberto.mini_authorizer.infrastructure.persistence.mapper.CardMapper;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CardRepositoryAdapter implements CardRepositoryPort {

    private final CardJpaRepository jpaRepository;
    private final CardMapper mapper;

    @Override
    public Card save(Card card) {
        CardEntity entity = mapper.toEntity(card);
        CardEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        return jpaRepository.findByCardNumber(cardNumber)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Card> findByCardNumberWithLock(String cardNumber) {
        return jpaRepository.findByCardNumberWithLock(cardNumber)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return jpaRepository.existsByCardNumber(cardNumber);
    }
}
