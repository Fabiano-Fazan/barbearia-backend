package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Products;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(

        @Schema(description = "Unique identifier of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the product", example = "Haircut")
        String name,

        @Schema(description = "Description of the product")
        String description,

        @Schema(description = "Category of the product")
        String category,

        @Schema(description = "Type of the product")
        String type,

        @Schema(description = "Indicates if the product is active")
        Boolean isActive,

        @Schema(description = "Price of the product", example = "29.99")
        BigDecimal price
) {
        public ProductResponseDTO(Products products){
                this(
                        products.getId(),
                        products.getName(),
                        products.getDescription(),
                        products.getCategory(),
                        products.getProductType().name(),
                        products.isActive(),
                        products.getPrice()
                );
        }
}
