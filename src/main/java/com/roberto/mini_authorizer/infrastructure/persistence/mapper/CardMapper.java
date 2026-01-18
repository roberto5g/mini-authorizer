package com.roberto.mini_authorizer.infrastructure.persistence.mapper;

import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardResponse;
import com.roberto.mini_authorizer.infrastructure.persistence.entity.CardEntity;
import com.roberto.mini_authorizer.domain.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardEntity toEntity(Card card) {
        CardEntity entity = new CardEntity();
        entity.setId(card.getId());
        entity.setCardNumber(card.getCardNumber());
        entity.setPassword(card.getPassword());
        entity.setBalance(card.getBalance());
        return entity;
    }

    public Card toDomain(CardEntity entity) {
        return new Card(
                entity.getId(),
                entity.getCardNumber(),
                entity.getPassword(),
                entity.getBalance()
        );
    }

    public static CardResponse toResponse(String cardNumber, String password){
        return new CardResponse(cardNumber, password);
    }
}
