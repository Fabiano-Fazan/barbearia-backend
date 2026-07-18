package com.barbearia.application.dto.request;

import com.barbearia.domain.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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
        @Size(min = 1, max = 60, message = "Duration must be between 1 and 60 minutes")
        int durationInMinutes,

        @NotNull(message = "Active status is required")
        @Schema(description = "Indicates if the product is active", example = "true")
        boolean isActive

) {
}
