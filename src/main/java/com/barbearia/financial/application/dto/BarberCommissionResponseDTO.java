package com.barbearia.financial.application.dto;

import com.barbearia.financial.domain.model.Commission;
import com.barbearia.financial.domain.model.CommissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BarberCommissionResponseDTO(

        @Schema(description = "The ID of the commission")
        UUID commissionId,

        @Schema(description = "The ID of the barber")
        UUID barberId,

        @Schema(description = "The name of the barber")
        String barberName,

        @Schema(description = "The ID of the appointment")
        UUID appointmentId,

        @Schema(description = "The amount of the service")
        BigDecimal serviceAmount,

        @Schema(description = "The commission rate")
        BigDecimal commissionRate,

        @Schema(description = "The commission amount")
        BigDecimal commissionAmount,

        @Schema(description = "The status of the commission")
        CommissionStatus status,

        @Schema(description = "The date and time the commission was created")
        LocalDateTime createdAt,

        @Schema(description = "The date and time the commission was paid")
        LocalDateTime paidAt
) {
    public BarberCommissionResponseDTO(Commission barberCommission) {
        this(
                barberCommission.getId(),
                barberCommission.getBarberId(),
                barberCommission.getBarberName(),
                barberCommission.getAppointmentId(),
                barberCommission.getServiceAmount(),
                barberCommission.getCommissionRate(),
                barberCommission.getCommissionAmount(),
                barberCommission.getStatus(),
                barberCommission.getCreatedAt(),
                barberCommission.getPaidAt()
        );
    }
}
