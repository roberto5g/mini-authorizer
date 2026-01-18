package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.exceptions.InsufficientBalanceException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SufficientBalanceValidator implements TransactionValidator {

    @Override
    public void validate(Transaction transaction, Card card) {
        Optional.of(card)
                .filter(c -> c.hasSufficientBalance(transaction.getAmount()))
                .orElseThrow(InsufficientBalanceException::new);
    }
}
