package com.ibrahim.ledger.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {

    private String description;

    @NotBlank
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{10,30}$", message = "Invalid IBAN format")
    private String counterPartyIban;

    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

}
