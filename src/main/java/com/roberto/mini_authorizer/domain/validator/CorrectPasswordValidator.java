package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.exceptions.InvalidPasswordException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CorrectPasswordValidator implements TransactionValidator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void validate(Transaction transaction, Card card) {
        Optional.of(transaction.getCardPassword())
                .filter(password -> passwordEncoder.matches(password, card.getPassword()))
                .orElseThrow(InvalidPasswordException::new);
    }
}
