package com.roberto.mini_authorizer.integration;

import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardRequest;
import com.roberto.mini_authorizer.infrastructure.persistence.repository.CardJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class CardIntegrationIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardJpaRepository cardJpaRepository;

    @Test
    void shouldCreateCardAndReturnBalance() throws Exception {

        cardJpaRepository.findByCardNumber("1234567890123456")
                .ifPresent(cardJpaRepository::delete);

        var request = new CardRequest(
                "1234567890123456",
                "1234"
        );

        mockMvc.perform(post("/cartoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("1234567890123456"))
                .andExpect(jsonPath("$.senha").value("1234"));

        mockMvc.perform(get("/cartoes/{cardNumber}", "1234567890123456")
                        .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00"));
    }
}

