package com.ibrahim.ledger.service;

import com.ibrahim.ledger.dto.TransactionRequestDto;
import com.ibrahim.ledger.dto.TransactionResponseDto;
import com.ibrahim.ledger.mapper.TransactionMapper;
import com.ibrahim.ledger.model.TransactionModel;
import com.ibrahim.ledger.model.UserModel;
import com.ibrahim.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto request, UserModel currentUser) {

        TransactionModel transaction = transactionMapper.toEntity(request);

        transaction.setUser(currentUser);
        transaction.setCreatedAt(Instant.now());

        TransactionModel saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getTransactions(UUID userId, Pageable pageable) {
        return transactionRepository
                .findAllByUser_UserId(userId, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getTransactionsByDateRange(UUID userId, Instant from, Instant to, Pageable pageable) {
        return transactionRepository
                .findAllByUser_UserIdAndCreatedAtBetween(userId, from, to, pageable)
                .map(transactionMapper::toResponse);
    }

}
