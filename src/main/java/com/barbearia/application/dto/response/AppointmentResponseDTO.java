package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Appointment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AppointmentResponseDTO(

        @Schema(description = "Unique identifier of the appointment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the client")
        String clientName,

        @Schema(description = "Name of the barber")
        String barberName,

        @Schema(description = "List of products for the appointment")
        List<ProductSummaryDTO> products,

        @Schema(description = "Start date and time of the appointment")
        LocalDateTime startTime,

        @Schema(description = "End date and time of the appointment")
        LocalDateTime endTime,

        @Schema(description = "Observation for the appointment")
        String observation,

        @Schema(description = "Price total of the appointment")
        BigDecimal price
) {
        public AppointmentResponseDTO(Appointment appointment) {
            this(
                    appointment.getId(),
                    appointment.getClient().getName(),
                    appointment.getBarber().getName(),
                    appointment.getProducts().stream().map(ProductSummaryDTO::new).toList(),
                    appointment.getStartTime(),
                    appointment.getEndTime(),
                    appointment.getObservation(),
                    appointment.getTotalPrice()
            );
        }
}
