package com.barbearia.staff.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BarberRequestDTO(

        @NotBlank(message = "Name is required")
        @Schema(description = "Name of the barber", example = "John Doe")
        String name,

        @NotBlank(message = "Phone is required")
        @Schema(description = "Phone of the barber", example = "1234567890")
        String phone,

        @NotBlank(message = "Email is required")
        @Schema(description = "Email of the barber", example = "john.doe@example.com")
        @Email(message = "Email should be valid")
        String email,

        @NotNull(message = "List of product IDs is required")
        @Schema(description = "List of product IDs associated with the barber")
        List<UUID> productsId,

        @NotNull(message = "Commission is required")
        @Schema(description = "Commission for the barber", example = "20.00%")
        BigDecimal commissionRate
) {
}
