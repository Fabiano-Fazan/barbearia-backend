package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Barber;
import com.barbearia.domain.entities.Products;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
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

        @Schema(description = "List of product IDs associated with the barber")
        List<UUID> productsId,

        @Schema(description = "Indicates if the barber is active", example = "true")
        Boolean isActive,

        @Schema(description = "Temporary password for the barber", example = "TempPass123")
        String temporaryPassword
) {
    public BarberResponseDTO(Barber barber) {
        this(
                barber.getId(),
                barber.getName(),
                barber.getPhone(),
                barber.getUser().getEmail(),
                barber.getSpecialties().stream().map(Products::getId).toList(),
                barber.getIsActive(),
                null
        );
    }
    public BarberResponseDTO(Barber barber, String temporaryPassword) {
        this(
                barber.getId(),
                barber.getName(),
                barber.getPhone(),
                barber.getUser().getEmail(),
                barber.getSpecialties().stream().map(Products::getId).toList(),
                barber.getIsActive(),
                temporaryPassword
        );
    }
}