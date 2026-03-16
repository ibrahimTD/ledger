package com.ibrahim.ledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class RegisterRequestDto {

    @NonNull
    private String userName;

    @NonNull
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NonNull
    @Email(message = "Email should be valid")
    private String email;

}
