package com.roberto.mini_authorizer.infrastructure.api.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CardRequest(

        @NotBlank
        @Pattern(regexp = "\\d{16}")
        @JsonProperty("numeroCartao")
        String cardNumber,

        @NotBlank
        @Size(min = 4)
        @JsonProperty("senha")
        String password
) {
}
