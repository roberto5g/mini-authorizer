package com.roberto.mini_authorizer.infrastructure.persistence.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CardEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCardEntitySuccessfully() {
        CardEntity card = new CardEntity();
        card.setCardNumber("1234567890123456");
        card.setPassword("encoded-password");
        card.setBalance(BigDecimal.valueOf(500));

        entityManager.persist(card);
        entityManager.flush();

        assertNotNull(card.getId());
        assertNotNull(card.getCreatedAt());
        assertNotNull(card.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsNull() {
        CardEntity card = new CardEntity();
        card.setPassword("password");
        card.setBalance(BigDecimal.valueOf(500));

        assertThrows(
                PersistenceException.class,
                () -> {
                    entityManager.persist(card);
                    entityManager.flush();
                }
        );
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsDuplicated() {
        CardEntity card1 = new CardEntity();
        card1.setCardNumber("1234567890123456");
        card1.setPassword("password");
        card1.setBalance(BigDecimal.valueOf(500));

        CardEntity card2 = new CardEntity();
        card2.setCardNumber("1234567890123456");
        card2.setPassword("password");
        card2.setBalance(BigDecimal.valueOf(300));

        entityManager.persist(card1);
        entityManager.flush();

        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    entityManager.persist(card2);
                    entityManager.flush();
                }
        );
    }


    @Test
    void shouldUpdateUpdatedAtOnUpdate() {
        CardEntity card = new CardEntity();
        card.setCardNumber("1234567890123456");
        card.setPassword("password");
        card.setBalance(BigDecimal.valueOf(500));

        entityManager.persist(card);
        entityManager.flush();

        LocalDateTime firstUpdatedAt = card.getUpdatedAt();

        card.setBalance(BigDecimal.valueOf(400));
        entityManager.merge(card);
        entityManager.flush();

        assertTrue(card.getUpdatedAt().isAfter(firstUpdatedAt));
    }
}
