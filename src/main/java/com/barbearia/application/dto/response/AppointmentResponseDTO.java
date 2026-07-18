package com.barbearia.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(

        UUID id,
        UUID clientId,
        UUID barberId,
        UUID productId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
