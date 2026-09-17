package ru.otus.hw.auth.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username may contain only letters, digits, '_', '.' and '-'")
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;
}
