package com.roberto.mini_authorizer.integration;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.infrastructure.persistence.repository.CardJpaRepository;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


class AuthorizationConcurrencyIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CardRepositoryPort cardRepository;
    @Autowired
    private CardJpaRepository cardJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        cardJpaRepository.findByCardNumber("1234567890123457")
                .ifPresent(cardJpaRepository::delete);

        Card card = new Card(
                "1234567890123457",
                passwordEncoder.encode("1234"),
                new BigDecimal("100.00")
        );

        cardRepository.save(card);
    }


    @Test
    void shouldPreventRaceConditionWithPessimisticLock() throws Exception {

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(post("/transacoes")
                                    .with(httpBasic("username", "password"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                        {
                                            "numeroCartao": "1234567890123457",
                                            "senhaCartao": "1234",
                                            "valor": 20.00
                                        }
                                        """))
                            .andExpect(result -> {
                                if (result.getResponse().getStatus() == 201) {
                                    success.incrementAndGet();
                                } else {
                                    failure.incrementAndGet();
                                }
                            });
                } catch (Exception e) {
                    failure.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        var card = cardRepository
                .findByCardNumber("1234567890123457")
                .orElseThrow();

        assertThat(success.get()).isEqualTo(5);
        assertThat(failure.get()).isEqualTo(5);
        assertThat(card.getBalance()).isEqualByComparingTo("0.00");
    }
}

