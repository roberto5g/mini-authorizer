package com.roberto.mini_authorizer.infrastructure.api.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotBlank
        @JsonProperty("numeroCartao")
        String cardNumber,

        @NotEmpty
        @JsonProperty("senhaCartao")
        String cardPassword,

        @NotNull
        @DecimalMin("0.01")
        @JsonProperty("valor")
        BigDecimal amount
) {
}
