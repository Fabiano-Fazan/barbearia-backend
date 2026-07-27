package com.barbearia.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AppointmentRequestDTO(

        @NotNull(message = "Client ID is required")
        @Schema(description = "ID of the client", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID clientId,

        @NotNull(message = "Barber ID is required")
        @Schema(description = "ID of the barber", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID barberId,

        @NotNull(message = "Product ID is required")
        @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        List<UUID> productId,

        @NotNull(message = "Start date is required")
        @Schema(description = "Start date and time of the appointment")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @NotNull(message = "End date is required")
        @Schema(description = "End date and time of the appointment")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,

        @Schema(description = "Observation for the appointment")
        String observation,

        @NotNull(message = "Price is required")
        @Schema(description = "Price of the appointment")
        BigDecimal price


) {
}
