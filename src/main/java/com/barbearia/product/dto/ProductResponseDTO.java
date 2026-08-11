package com.barbearia.product.dto;

import com.barbearia.product.Product;
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

        @Schema(description = "Duration of the product in minutes", example = "30")
        Integer durationInMinutes,

        @Schema(description = "Price of the product", example = "29.99")
        BigDecimal price
) {
        public ProductResponseDTO(Product product){
                this(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getCategory(),
                        product.getProductType().name(),
                        product.getDurationInMinutes(),
                        product.getPrice()
                );
        }
}
