package com.ibrahim.ledger.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequestDto {
    @NonNull
    private String userName;

    @NonNull
    private String password;
}
