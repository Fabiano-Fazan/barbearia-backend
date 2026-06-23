package com.barbearia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ClientResponseDTO(
        @Schema(description = "Unique identifier of the client", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the client", example = "Fabiano Fazan")
        String name,

        @Schema(description = "Email of the client", example = "fabiano.fazan@example.com")
        String email,

        @Schema(description = "Phone of the client", example = "(11) 99999-9999")
        String phone,

        @Schema(description = "Address of the client", example = "123 Main St, City, State")
        String address
) {
}
