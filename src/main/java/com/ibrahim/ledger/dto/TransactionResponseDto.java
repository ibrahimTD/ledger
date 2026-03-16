package com.ibrahim.ledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Data
public class TransactionResponseDto {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String counterPartyIban;
    private Instant createdAt;
}
