package com.barbearia.financial.dto;

import com.barbearia.financial.BarberCommission;
import com.barbearia.financial.CommissionStatus;
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
    public BarberCommissionResponseDTO(BarberCommission barberCommission) {
        this(
                barberCommission.getId(),
                barberCommission.getBarber().getId(),
                barberCommission.getBarber().getName(),
                barberCommission.getAppointment().getId(),
                barberCommission.getAppointment().getTotalPrice(),
                barberCommission.getBarber().getCommissionRate(),
                barberCommission.getCommissionAmount(),
                barberCommission.getStatus(),
                barberCommission.getCreatedAt(),
                barberCommission.getPaidAt()
        );
    }
}