package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;

@FunctionalInterface
public interface TransactionValidator {
    void validate(Transaction transaction, Card card);
}
