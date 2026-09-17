package ru.otus.hw.auth.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username must not be empty")
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;
}
