package com.roberto.mini_authorizer.infrastructure.api.controllers;

import com.roberto.mini_authorizer.infrastructure.api.controllers.dtos.TransactionRequest;
import com.roberto.mini_authorizer.domain.model.Transaction;
import com.roberto.mini_authorizer.ports.in.TransactionServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServicePort transactionService;

    @PostMapping
    public ResponseEntity<String> authorize(
            @Valid @RequestBody TransactionRequest request) {

        Transaction transaction = new Transaction(
                request.cardNumber(),
                request.cardPassword(),
                request.amount()
        );

        transactionService.authorize(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
