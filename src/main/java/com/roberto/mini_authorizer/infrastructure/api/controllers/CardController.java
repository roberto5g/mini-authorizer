package com.roberto.mini_authorizer.infrastructure.api.controllers;

import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardRequest;
import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.CardResponse;
import com.roberto.mini_authorizer.infrastructure.persistence.mapper.CardMapper;
import com.roberto.mini_authorizer.ports.in.CardServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CardController {

    private final CardServicePort cardServicePort;


    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardRequest request) {
        var card = cardServicePort.createCard(
                request.cardNumber(),
                request.password()
        );
        var response = CardMapper.toResponse(request.cardNumber(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cardNumber}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String cardNumber) {
        BigDecimal balance = cardServicePort.getBalance(cardNumber);
        return ResponseEntity.ok(balance);
    }
}
