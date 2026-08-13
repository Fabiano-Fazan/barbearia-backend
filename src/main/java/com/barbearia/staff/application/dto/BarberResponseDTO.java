package com.barbearia.staff.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
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

        @Schema(description = "Commission for the barber", example = "20.00%")
        BigDecimal commissionRate,

        @Schema(description = "Temporary password for the barber", example = "TempPass123")
        String temporaryPassword
) {}
