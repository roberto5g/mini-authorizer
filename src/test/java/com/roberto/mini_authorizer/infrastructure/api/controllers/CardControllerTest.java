package com.roberto.mini_authorizer.infrastructure.api.controllers;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardRequest;
import com.roberto.mini_authorizer.infrastructure.config.SecurityConfig;
import com.roberto.mini_authorizer.ports.in.CardServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardServicePort cardServicePort;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldCreateCardSuccessfully() throws Exception {
        var request = new CardRequest(
                "1234567890123456",
                "1234"
        );

        var card = new Card(
                "1234567890123456",
                "1234",
                BigDecimal.valueOf(500)
        );

        when(cardServicePort.createCard(anyString(), anyString()))
                .thenReturn(card);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("1234567890123456"))
                .andExpect(jsonPath("$.senha").value("1234"));

        verify(cardServicePort).createCard("1234567890123456", "1234");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        var invalidRequest = new CardRequest(
                "123",
                "12"
        );

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cardServicePort);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnBalanceSuccessfully() throws Exception {
        when(cardServicePort.getBalance("1234567890123456"))
                .thenReturn(BigDecimal.valueOf(250.75));

        mockMvc.perform(get("/cartoes/{cardNumber}", "1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("250.75"));

        verify(cardServicePort).getBalance("1234567890123456");
    }
}

