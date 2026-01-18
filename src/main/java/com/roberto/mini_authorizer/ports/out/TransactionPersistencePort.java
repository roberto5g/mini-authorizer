package com.roberto.mini_authorizer.ports.out;

import java.util.Optional;

public interface TransactionPersistencePort {
    boolean hasBeenProcessed(String idempotencyKey);
    void markAsProcessed(String idempotencyKey);
    Optional<String> findResult(String idempotencyKey);
}
