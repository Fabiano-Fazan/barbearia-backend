package com.barbearia.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(

        @NotBlank(message = "Client ID is required")
        @Schema(description = "ID of the client", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID clientId,

        @NotBlank(message = "Barber ID is required")
        @Schema(description = "ID of the barber", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID barberId,

        @NotBlank(message = "Product ID is required")
        @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID productId,

        @NotBlank(message = "Start date is required")
        @Schema(description = "Start date and time of the appointment")
        LocalDateTime startDate,

        @NotBlank(message = "End date is required")
        @Schema(description = "End date and time of the appointment")
        LocalDateTime endDate
) {
}
