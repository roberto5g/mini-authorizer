package com.roberto.mini_authorizer.application.service;

import com.roberto.mini_authorizer.domain.exceptions.CardNotFoundForTransactionException;
import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import com.roberto.mini_authorizer.domain.validator.ValidatorChain;
import com.roberto.mini_authorizer.ports.in.TransactionServicePort;
import com.roberto.mini_authorizer.ports.out.CardRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionServicePort {

    private final CardRepositoryPort cardRepository;
    private final ValidatorChain validatorChain;

    @Override
    @Transactional
    public void authorize(Transaction transaction) {

        Card card = cardRepository
                .findByCardNumberWithLock(transaction.getCardNumber())
                .orElseThrow(CardNotFoundForTransactionException::new);

        validatorChain.validate(transaction, card);

        card.debit(transaction.getAmount());

        cardRepository.save(card);
    }
}
