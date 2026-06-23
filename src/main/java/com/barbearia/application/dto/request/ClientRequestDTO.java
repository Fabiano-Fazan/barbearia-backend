package com.barbearia.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record ClientRequestDTO(

        @NotBlank(message = "Name is required")
        @Schema(description = "Name of the client", example = "Fabiano Fazan")
        String name,

        @NotBlank(message = "Email is required")
        @Schema(description = "Email of the client", example = "fabiano.fazan@example.com")
        String email,

        @Schema(description = "Phone of the client", example = "1234567890")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?[\\s-]?9?\\d{4}-?\\d{4}$",
                message = "Phone number must be in the format (XX) XXXXX-XXXX or (XX) XXXX-XXXX"
        )
        String phone
) {
}
