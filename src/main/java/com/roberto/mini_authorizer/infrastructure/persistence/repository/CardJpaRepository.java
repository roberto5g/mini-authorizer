package com.roberto.mini_authorizer.infrastructure.persistence.repository;

import com.roberto.mini_authorizer.infrastructure.persistence.entity.CardEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    @Query(value = "SELECT * FROM cards WHERE card_number = :cardNumber FOR UPDATE", nativeQuery = true)
    Optional<CardEntity> findByCardNumberWithLock(@Param("cardNumber") String cardNumber);

}
