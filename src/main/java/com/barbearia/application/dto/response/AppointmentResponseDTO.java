package com.barbearia.application.dto.response;

import com.barbearia.domain.entities.Appointment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(

        @Schema(description = "Unique identifier of the appointment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the client", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID clientId,

        @Schema(description = "ID of the barber", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID barberId,

        @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID productId,

        @Schema(description = "Start date and time of the appointment")
        LocalDateTime startTime,

        @Schema(description = "End date and time of the appointment")
        LocalDateTime endTime,

        @Schema(description = "Observation for the appointment")
        String observation
) {
        public AppointmentResponseDTO(Appointment appointment) {
            this(
                    appointment.getId(),
                    appointment.getClient().getId(),
                    appointment.getBarber().getId(),
                    appointment.getProducts().getId(),
                    appointment.getStartTime(),
                    appointment.getEndTime(),
                    appointment.getObservation()
            );
        }
}
