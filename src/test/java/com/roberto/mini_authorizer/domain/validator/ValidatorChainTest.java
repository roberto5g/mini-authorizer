package com.roberto.mini_authorizer.domain.validator;

import com.roberto.mini_authorizer.domain.model.Card;
import com.roberto.mini_authorizer.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorChainTest {

    @Mock
    TransactionValidator validator1;

    @Mock
    TransactionValidator validator2;

    @Mock
    TransactionValidator validator3;

    @Mock
    Card card;

    @Mock
    Transaction transaction;

    ValidatorChain validatorChain;

    @BeforeEach
    void setup() {
        validatorChain = new ValidatorChain(
                List.of(validator1, validator2, validator3)
        );
    }

    @Test
    void shouldExecuteAllValidators() {
        validatorChain.validate(transaction, card);

        verify(validator1).validate(transaction, card);
        verify(validator2).validate(transaction, card);
        verify(validator3).validate(transaction, card);
    }

    @Test
    void shouldExecuteValidatorsInOrder() {
        validatorChain.validate(transaction, card);

        InOrder inOrder = inOrder(validator1, validator2, validator3);

        inOrder.verify(validator1).validate(transaction, card);
        inOrder.verify(validator2).validate(transaction, card);
        inOrder.verify(validator3).validate(transaction, card);
    }

    @Test
    void shouldStopExecutionWhenValidatorThrowsException() {
        RuntimeException exception = new RuntimeException("validation failed");

        doThrow(exception)
                .when(validator2)
                .validate(transaction, card);

        assertThrows(
                RuntimeException.class,
                () -> validatorChain.validate(transaction, card)
        );

        verify(validator1).validate(transaction, card);
        verify(validator2).validate(transaction, card);
        verify(validator3, never()).validate(any(), any());
    }


}