package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Barber;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record BarberResponseDTO(

        @Schema(description = "Unique identifier of the barber", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the barber", example = "John Doe")
        String name,

        @Schema(description = "Phone number of the barber", example = "(11) 99999-9999")
        String phone,

        @Schema(description = "Email of the barber", example = "john.doe@example.com")
        String email,

        @Schema(description = "Temporary password for the barber", example = "TempPass123")
        String temporaryPassword
) {
    public BarberResponseDTO(Barber barber, String temporaryPassword) {
        this(
                barber.getId(),
                barber.getName(),
                barber.getUser().getEmail(),
                barber.getPhone(),
                temporaryPassword
        );
    }
}