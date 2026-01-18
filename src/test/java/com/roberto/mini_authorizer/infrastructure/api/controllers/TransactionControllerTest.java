package com.roberto.mini_authorizer.infrastructure.api.controllers;

import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundForTransactionException;
import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import com.roberto.mini_authorizer.domain.exceptions.InvalidPasswordException;
import com.roberto.mini_authorizer.domain.model.Transaction;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.TransactionRequest;
import com.roberto.mini_authorizer.infrastructure.config.SecurityConfig;
import com.roberto.mini_authorizer.ports.in.TransactionServicePort;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionServicePort transactionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldAuthorizeTransactionSuccessfully() throws Exception {

        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                new BigDecimal("100.00")
        );

        doNothing().when(transactionService).authorize(any(Transaction.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        verify(transactionService).authorize(any(Transaction.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        var invalidRequest = new TransactionRequest(
                "",                  // @NotBlank
                "",                  // @NotEmpty
                BigDecimal.ZERO      // @DecimalMin("0.01")
        );

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn422WhenInsufficientBalance() throws Exception {

        var request = new TransactionRequest(
                "1234567890123456",
                "1234",
                new BigDecimal("100.00")
        );

        doThrow(new InsufficientBalanceException())
                .when(transactionService)
                .authorize(any(Transaction.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));

        verify(transactionService).authorize(any(Transaction.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn422WhenPasswordIsInvalid() throws Exception {

        var request = new TransactionRequest(
                "1234567890123456",
                "9999",
                new BigDecimal("50.00")
        );

        doThrow(new InvalidPasswordException())
                .when(transactionService)
                .authorize(any(Transaction.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));

        verify(transactionService).authorize(any(Transaction.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldReturn422WhenCardDoesNotExist() throws Exception {

        var request = new TransactionRequest(
                "0000000000000000",
                "1234",
                new BigDecimal("10.00")
        );

        doThrow(new CardNotFoundForTransactionException())
                .when(transactionService)
                .authorize(any(Transaction.class));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string("CARTAO_INEXISTENTE"));

        verify(transactionService).authorize(any(Transaction.class));
    }


}
