package com.barbearia.catalog.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryDTO(

        @Schema(description = "Unique identifier of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the product", example = "Haircut")
        String name,

        @Schema(description = "Price of the product", example = "25.00")
        BigDecimal price,

        @Schema(description = "Duration of the product in minutes", example = "30")
        Integer durationInMinutes

) {}
