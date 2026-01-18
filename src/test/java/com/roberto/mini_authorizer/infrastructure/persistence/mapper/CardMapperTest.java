package com.roberto.mini_authorizer.infrastructure.persistence.mapper;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardResponse;
import com.roberto.mini_authorizer.infrastructure.persistence.entity.CardEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardMapperTest {

    private CardMapper mapper;
    private Long id;


    @BeforeEach
    void setUp() {
        mapper = new CardMapper();
        id = 1L;
    }

    @Test
    void shouldMapDomainToEntity() {

        Card card = new Card(
                id,
                "1234567890123456",
                "encoded-password",
                BigDecimal.valueOf(500)
        );

        CardEntity entity = mapper.toEntity(card);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(card.getCardNumber(), entity.getCardNumber());
        assertEquals(card.getPassword(), entity.getPassword());
        assertEquals(card.getBalance(), entity.getBalance());
    }

    @Test
    void shouldMapEntityToDomain() {
        CardEntity entity = new CardEntity();
        entity.setId(id);
        entity.setCardNumber("1234567890123456");
        entity.setPassword("encoded-password");
        entity.setBalance(BigDecimal.valueOf(300));

        Card card = mapper.toDomain(entity);

        assertNotNull(card);
        assertEquals(entity.getId(), card.getId());
        assertEquals(entity.getCardNumber(), card.getCardNumber());
        assertEquals(entity.getPassword(), card.getPassword());
        assertEquals(
                0,
                BigDecimal.valueOf(300).compareTo(card.getBalance())
        );
    }

    @Test
    void shouldCreateCardResponse() {
        String cardNumber = "1234567890123456";
        String password = "plain-password";

        CardResponse response = CardMapper.toResponse(cardNumber, password);

        assertNotNull(response);
        assertEquals(cardNumber, response.cardNumber());
        assertEquals(password, response.password());
    }
}
