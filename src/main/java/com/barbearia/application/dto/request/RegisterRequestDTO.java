package com.barbearia.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank
        @Schema(description = "Email of the user", example = "john.doe@example.com")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank
        @Schema(description = "Name of the user", example = "John Doe")
        String name,

        @NotBlank
        @Schema(description = "Password of the user", example = "password123")
        String password
) {
}
