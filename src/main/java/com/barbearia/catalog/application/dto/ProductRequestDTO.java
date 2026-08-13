package com.barbearia.catalog.application.dto;

import com.barbearia.catalog.domain.model.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank(message = "Name is required")
        @Schema(description = "Name of the product", example = "Haircut")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Schema(description = "Description of the product", example = "A stylish haircut")
        @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
        String description,

        @NotNull(message = "Price is required")
        @Schema(description = "Price of the product", example = "29.99")
        @Positive(message = "Price must be a positive value")
        BigDecimal price,

        @NotNull(message = "Type is required")
        @Schema(description = "Type of the product", example = "SERVICE")
        ProductType type,

        @NotBlank(message = "Category is required")
        @Schema(description = "Category of the product", example = "HAIRCUT")
        @Size(min = 10, max = 50, message = "Category must be between 10 and 50 characters")
        String category,

        @NotNull(message = "Duration in minutes is required")
        @Schema(description = "Duration of the product in minutes", example = "30")
        @Max(value = 60, message = "Duration must not exceed 60 minutes")
        @Min(value = 5, message = "Duration must be at least 5 minutes")
        Integer durationInMinutes

) {
}
