package com.barbearia.financial.application.dto;

import com.barbearia.financial.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PayCommissionRequestDTO
        (
        @NotNull(message = "Commission IDs are required")
        @Schema(description = "List of commission IDs to be paid", example = "[\"550e8400-e29b-41d4-a716-446655440000\"," +
                " \"550e8400-e29b-41d4-a716-446655440001\"]")
        List<UUID> commissionId,

        @NotNull(message = "Payment method is required")
        @Schema (description = "Payment method to be used", example = "CREDIT_CARD")
        PaymentMethod paymentMethod
) {
}
