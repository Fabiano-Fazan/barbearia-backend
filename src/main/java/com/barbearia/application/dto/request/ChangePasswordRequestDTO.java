package com.barbearia.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDTO (

        @NotBlank(message = "New password is required")
        @Schema(description = "New password of the user", example = "newPassword123")
        String newPassword
){
}
