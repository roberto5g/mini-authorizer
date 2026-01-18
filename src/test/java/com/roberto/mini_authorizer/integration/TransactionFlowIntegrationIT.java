package com.roberto.mini_authorizer.integration;

import com.roberto.mini_authorizer.infrastructure.persistence.repository.CardJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class TransactionFlowIntegrationIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardJpaRepository cardJpaRepository;

    @BeforeEach
    void setup() {
        cardJpaRepository.findByCardNumber("1234567890123456")
                .ifPresent(cardJpaRepository::delete);
        cardJpaRepository.findByCardNumber("1111222233334444")
                .ifPresent(cardJpaRepository::delete);
        cardJpaRepository.findByCardNumber("9999888877776666")
                .ifPresent(cardJpaRepository::delete);
    }

    @Test
    void shouldCreateCardAuthorizeTransactionAndUpdateBalance() throws Exception {

        var createCardRequest = """
        {
          "numeroCartao": "1234567890123456",
          "senha": "1234"
        }
        """;

        mockMvc.perform(post("/cartoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCardRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/cartoes/{cardNumber}", "1234567890123456")
                        .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00"));

        var transactionRequest = """
        {
          "numeroCartao": "1234567890123456",
          "senhaCartao": "1234",
          "valor": 100.00
        }
        """;

        mockMvc.perform(post("/transacoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        mockMvc.perform(get("/cartoes/{cardNumber}", "1234567890123456")
                        .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("400.00"));
    }

    @Test
    void shouldReturn422WhenBalanceIsInsufficient() throws Exception {

        mockMvc.perform(post("/cartoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "numeroCartao": "1111222233334444",
                          "senha": "1234"
                        }
                    """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transacoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "numeroCartao": "1111222233334444",
                          "senhaCartao": "1234",
                          "valor": 1000.00
                        }
                    """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    void shouldReturn422WhenPasswordIsInvalid() throws Exception {

        mockMvc.perform(post("/cartoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "numeroCartao": "9999888877776666",
                          "senha": "1234"
                        }
                    """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transacoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "numeroCartao": "9999888877776666",
                          "senhaCartao": "0000",
                          "valor": 10.00
                        }
                    """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));
    }

    @Test
    void shouldReturn422WhenCardDoesNotExist() throws Exception {

        mockMvc.perform(post("/transacoes")
                        .with(httpBasic("username", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "numeroCartao": "0000000000000000",
                          "senhaCartao": "1234",
                          "valor": 10.00
                        }
                    """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string("CARTAO_INEXISTENTE"));
    }


}

