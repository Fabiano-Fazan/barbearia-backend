package com.barbearia.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BarberResquestDTO(

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

        @NotNull(message = "Active status is required")
        @Schema(description = "Indicates if the barber is active", example = "true")
        Boolean isActive

) {
}
