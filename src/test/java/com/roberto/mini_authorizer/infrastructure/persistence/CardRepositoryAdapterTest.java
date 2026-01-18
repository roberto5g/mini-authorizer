package com.roberto.mini_authorizer.infrastructure.persistence;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.infrastructure.persistence.entity.CardEntity;
import com.roberto.mini_authorizer.infrastructure.persistence.mapper.CardMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({CardRepositoryAdapter.class, CardMapper.class})
class CardRepositoryAdapterTest {

    @Autowired
    private CardRepositoryAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveCardSuccessfully() {
        Card card = Card.create(
                "1234567890123456",
                "1234"
        );

        Card saved = adapter.save(card);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("1234567890123456", saved.getCardNumber());
        assertEquals(new BigDecimal("500.00"), saved.getBalance());
    }

    @Test
    void shouldFindCardByCardNumber() {
        CardEntity entity = new CardEntity();
        entity.setCardNumber("1234567890123456");
        entity.setPassword("1234");
        entity.setBalance(new BigDecimal("500.00"));

        entityManager.persist(entity);
        entityManager.flush();

        Optional<Card> result =
                adapter.findByCardNumber("1234567890123456");

        assertTrue(result.isPresent());
        assertEquals("1234567890123456", result.get().getCardNumber());
        assertEquals(new BigDecimal("500.00"), result.get().getBalance());
    }

    @Test
    void shouldReturnEmptyWhenCardDoesNotExist() {
        Optional<Card> result =
                adapter.findByCardNumber("9999999999999999");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenCardExists() {
        CardEntity entity = new CardEntity();
        entity.setCardNumber("1234567890123456");
        entity.setPassword("1234");
        entity.setBalance(new BigDecimal("500.00"));

        entityManager.persist(entity);
        entityManager.flush();

        boolean exists =
                adapter.existsByCardNumber("1234567890123456");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCardDoesNotExist() {
        boolean exists =
                adapter.existsByCardNumber("0000000000000000");

        assertFalse(exists);
    }

    @Test
    void shouldFindCardByCardNumberWithLock() {
        CardEntity entity = new CardEntity();
        entity.setCardNumber("1234567890123456");
        entity.setPassword("1234");
        entity.setBalance(new BigDecimal("500.00"));

        entityManager.persist(entity);
        entityManager.flush();

        Optional<Card> result =
                adapter.findByCardNumberWithLock("1234567890123456");

        assertTrue(result.isPresent());
    }
}
