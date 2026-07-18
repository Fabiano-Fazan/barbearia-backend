package com.barbearia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(

        @Schema(description = "Unique identifier of the appointment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the client", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID clientId,

        @Schema(description = "ID of the barber", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID barberId,

        @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID productId,

        @Schema(description = "Start date and time of the appointment")
        LocalDateTime startDate,

        @Schema(description = "End date and time of the appointment")
        LocalDateTime endDate
) {
}
