package com.ibrahim.ledger.resources;

import com.ibrahim.ledger.dto.TransactionRequestDto;
import com.ibrahim.ledger.dto.TransactionResponseDto;
import com.ibrahim.ledger.model.UserModel;
import com.ibrahim.ledger.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Create and retrieve transactions")
@SecurityRequirement(name = "Bearer Auth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a transaction")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @Valid @RequestBody TransactionRequestDto request,
            @AuthenticationPrincipal UserModel currentUser
    ) {
        TransactionResponseDto response = transactionService.createTransaction(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get paginated transactions", description = "Optionally filter by date range using 'from' and 'to' params")
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(
            @AuthenticationPrincipal UserModel currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (from != null && to != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByDateRange(currentUser.getUserId(), from, to, pageable));
        }

        return ResponseEntity.ok(transactionService.getTransactions(currentUser.getUserId(), pageable));
    }

}
