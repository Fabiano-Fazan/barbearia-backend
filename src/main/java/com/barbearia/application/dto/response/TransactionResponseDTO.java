package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Financial;
import com.barbearia.domain.enums.PaymentMethod;
import com.barbearia.domain.enums.TransactionCategory;
import com.barbearia.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(

        @Schema(description = "Transaction ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID transactionId,

        @Schema(description = "Transaction description", example = "Payment for haircut")
        String description,

        @Schema(description = "Transaction type", example = "INCOME")
        TransactionType type,

        @Schema(description = "Transaction category", example = "SERVICE")
        TransactionCategory category,

        @Schema(description = "Payment method", example = "CREDIT_CARD")
        PaymentMethod method,

        @Schema(description = "Transaction date", example = "2023-01-01T10:00:00")
        LocalDateTime transactionDate,

        @Schema(description = "Paid date", example = "2023-01-01T10:00:00")
        LocalDateTime paidDate,

        @Schema(description = "Barber ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID barberId,

        @Schema(description = "Appointment ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID appointmentId,

        @Schema(description = "Barber name", example = "John Doe")
        String barberName
) {
    public TransactionResponseDTO(Financial transaction) {
        this(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getPaymentMethod(),
                transaction.getTransactionDate(),
                transaction.getPaidDate(),
                transaction.getBarber().getId(),
                transaction.getAppointment().getId(),
                transaction.getBarber().getName()
        );
    }
}
