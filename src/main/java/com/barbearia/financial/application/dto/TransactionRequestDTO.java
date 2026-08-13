package com.barbearia.financial.application.dto;

import com.barbearia.financial.domain.model.PaymentMethod;
import com.barbearia.financial.domain.model.TransactionCategory;
import com.barbearia.financial.domain.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestDTO(

        @NotBlank(message = "Description is required")
        @Schema(description = "Description of the transaction")
        String description,

        @NotNull(message = "Amount is required")
        @Schema(description = "Amount of the transaction")
        BigDecimal amount,

        @NotNull(message = "Type is required")
        @Schema(description = "Type of the transaction",example = "INCOME")
        TransactionType type,

        @NotNull(message = "Category is required")
        @Schema(description = "Category of the transaction",example = "SALE_SERVICE")
        TransactionCategory category,

        @NotNull(message = "Payment method is required")
        @Schema(description = "Payment method of the transaction",example = "CREDIT_CARD")
        PaymentMethod paymentMethod,

        @Schema(description = "ID of the barber associated with the transaction",  example = "550e8400-e29b-41d4-a716-446655440000")
        UUID barberId,

        @Schema(description = "ID of the appointment associated with the transaction",  example = "550e8400-e29b-41d4-a716-446655440000")
        UUID appointmentId

) {
}
