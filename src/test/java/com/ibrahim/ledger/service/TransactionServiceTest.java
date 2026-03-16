package com.ibrahim.ledger.service;

import com.ibrahim.ledger.dto.TransactionRequestDto;
import com.ibrahim.ledger.dto.TransactionResponseDto;
import com.ibrahim.ledger.mapper.TransactionMapper;
import com.ibrahim.ledger.model.TransactionModel;
import com.ibrahim.ledger.model.UserModel;
import com.ibrahim.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    @InjectMocks private TransactionService transactionService;

    private UserModel currentUser;
    private TransactionRequestDto request;
    private TransactionModel transactionModel;
    private TransactionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        currentUser = new UserModel();
        currentUser.setUserId(UUID.randomUUID());
        currentUser.setUserName("ibrahim");

        request = new TransactionRequestDto();
        request.setAmount(new BigDecimal("1500.00"));
        request.setCurrency("USD");
        request.setDescription("Test payment");
        request.setCounterPartyIban("GB29NWBK60161331926819");

        transactionModel = new TransactionModel();
        transactionModel.setId(UUID.randomUUID());
        transactionModel.setUser(currentUser);
        transactionModel.setAmount(new BigDecimal("1500.00"));
        transactionModel.setCurrency("USD");
        transactionModel.setCreatedAt(Instant.now());

        responseDto = new TransactionResponseDto();
        responseDto.setId(transactionModel.getId());
        responseDto.setUserId(currentUser.getUserId());
        responseDto.setAmount(new BigDecimal("1500.00"));
        responseDto.setCurrency("USD");
        responseDto.setCreatedAt(transactionModel.getCreatedAt());
    }

    @Test
    void createTransaction_validRequest_returnsMappedResponse() {
        when(transactionMapper.toEntity(request)).thenReturn(transactionModel);
        when(transactionRepository.save(transactionModel)).thenReturn(transactionModel);
        when(transactionMapper.toResponse(transactionModel)).thenReturn(responseDto);

        TransactionResponseDto result = transactionService.createTransaction(request, currentUser);

        assertThat(result.getUserId()).isEqualTo(currentUser.getUserId());
        assertThat(result.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void createTransaction_injectsCurrentUserAndTimestamp() {
        TransactionModel blank = new TransactionModel();
        when(transactionMapper.toEntity(request)).thenReturn(blank);
        when(transactionRepository.save(any())).thenReturn(blank);
        when(transactionMapper.toResponse(any())).thenReturn(responseDto);

        transactionService.createTransaction(request, currentUser);

        assertThat(blank.getUser()).isEqualTo(currentUser);
        assertThat(blank.getCreatedAt()).isNotNull();
    }

    @Test
    void createTransaction_savesExactlyOnce() {
        when(transactionMapper.toEntity(request)).thenReturn(transactionModel);
        when(transactionRepository.save(any())).thenReturn(transactionModel);
        when(transactionMapper.toResponse(any())).thenReturn(responseDto);

        transactionService.createTransaction(request, currentUser);

        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void getTransactions_returnsPagedResultsForUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionModel> page = new PageImpl<>(List.of(transactionModel));

        when(transactionRepository.findAllByUser_UserId(currentUser.getUserId(), pageable)).thenReturn(page);
        when(transactionMapper.toResponse(transactionModel)).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getTransactions(currentUser.getUserId(), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(currentUser.getUserId());
    }

    @Test
    void getTransactions_noTransactions_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findAllByUser_UserId(any(), any())).thenReturn(Page.empty());

        Page<TransactionResponseDto> result = transactionService.getTransactions(currentUser.getUserId(), pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void getTransactionsByDateRange_returnsTransactionsWithinRange() {
        Instant from = Instant.parse("2026-03-15T00:00:00Z");
        Instant to   = Instant.parse("2026-03-16T23:59:59Z");
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionModel> page = new PageImpl<>(List.of(transactionModel));

        when(transactionRepository.findAllByUser_UserIdAndCreatedAtBetween(currentUser.getUserId(), from, to, pageable)).thenReturn(page);
        when(transactionMapper.toResponse(transactionModel)).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getTransactionsByDateRange(currentUser.getUserId(), from, to, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(currentUser.getUserId());
    }

    @Test
    void getTransactionsByDateRange_emptyRange_returnsEmptyPage() {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to   = Instant.parse("2026-01-02T00:00:00Z");
        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAllByUser_UserIdAndCreatedAtBetween(any(), any(), any(), any())).thenReturn(Page.empty());

        Page<TransactionResponseDto> result = transactionService.getTransactionsByDateRange(currentUser.getUserId(), from, to, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}

