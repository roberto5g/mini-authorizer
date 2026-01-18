package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidatorChain {

    private final List<TransactionValidator> validators;

    public ValidatorChain(List<TransactionValidator> validators) {
        this.validators = validators;
    }

    public void validate(Transaction transaction, Card card) {
        validators.forEach(validator -> validator.validate(transaction, card));
    }
}
