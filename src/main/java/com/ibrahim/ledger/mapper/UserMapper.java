package com.ibrahim.ledger.mapper;

import com.ibrahim.ledger.dto.RegisterRequestDto;
import com.ibrahim.ledger.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserModel toEntity(RegisterRequestDto registerRequestDto);

}
