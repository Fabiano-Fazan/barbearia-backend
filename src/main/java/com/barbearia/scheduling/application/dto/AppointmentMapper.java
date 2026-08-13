package com.barbearia.scheduling.application.dto;

import com.barbearia.scheduling.domain.model.Appointment;
import com.barbearia.catalog.application.dto.ProductSummaryDTO;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentResponseDTO toResponse(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(), appointment.getClientName(), appointment.getBarberName(),
                appointment.getProducts().stream()
                        .map(p -> new ProductSummaryDTO(p.productId(), p.name(), p.price(), p.durationInMinutes()))
                        .toList(),
                appointment.getStartTime(), appointment.getEndTime(), appointment.getObservation(), appointment.getTotalPrice());
    }
}
