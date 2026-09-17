package ru.otus.hw.auth.services.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 100, message = "Display name must not exceed 100 characters")
    private String displayName;

    @Email(message = "Email must be a valid address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
}
