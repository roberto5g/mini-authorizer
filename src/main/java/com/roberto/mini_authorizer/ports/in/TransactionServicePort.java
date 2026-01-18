package com.roberto.mini_authorizer.ports.in;

import com.roberto.mini_authorizer.domain.model.Transaction;

public interface TransactionServicePort {
    void authorize(Transaction transaction);
}
