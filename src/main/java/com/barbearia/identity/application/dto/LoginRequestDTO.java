package com.barbearia.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank
        @Schema(description = "Email of the client", example = "fabiano.fazan@example.com")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank
        @Schema(description = "Password of the client", example = "password123")
        String password
) {
}
