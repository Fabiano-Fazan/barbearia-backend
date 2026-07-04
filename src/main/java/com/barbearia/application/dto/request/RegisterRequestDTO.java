package com.barbearia.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotBlank
        String password
) {
}
