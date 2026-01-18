package com.roberto.mini_authorizer.infrastructure.api.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardResponse(
        @JsonProperty("numeroCartao")
        String cardNumber,
        @JsonProperty("senha")
        String password
) {
}

