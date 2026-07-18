package com.barbearia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponseDTO(

        @Schema(description = "Timestamp of the error", example = "2024-06-01T12:34:56")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code")
        int status,

        @Schema(description = "Error type")
        String error,

        @Schema(description = "Error message")
        String message,

        @Schema(description = "Requested path")
        String path
) {
}
