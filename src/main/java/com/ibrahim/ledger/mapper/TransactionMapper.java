package com.ibrahim.ledger.mapper;

import com.ibrahim.ledger.dto.TransactionRequestDto;
import com.ibrahim.ledger.dto.TransactionResponseDto;
import com.ibrahim.ledger.model.TransactionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    TransactionModel toEntity(TransactionRequestDto request);

    @Mapping(target = "userId", source = "user.userId")
    TransactionResponseDto toResponse(TransactionModel transaction);

}
