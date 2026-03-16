package com.ibrahim.ledger.repository;

import com.ibrahim.ledger.model.TransactionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, UUID> {

    Page<TransactionModel> findAllByUser_UserId(UUID userId, Pageable pageable);

    Page<TransactionModel> findAllByUser_UserIdAndCreatedAtBetween(UUID userId, Instant from, Instant to,Pageable pageable);
}
