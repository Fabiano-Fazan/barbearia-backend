package com.barbearia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponseDTO(

        @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Indicates if the user must change their password", example = "false")
        boolean mustChangePassword
) {
}
