package com.barbearia.appointment;

import com.barbearia.product.Product;
import com.barbearia.core.exceptions.AppointmentConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentValidation {

    private final AppointmentRepository appointmentRepository;

    public LocalDateTime calculateEndTime(Appointment appointment) {
        LocalDateTime startTime = appointment.getStartTime();
        return startTime.plusMinutes(
                appointment.getProducts()
                        .stream()
                        .mapToInt(Product::getDurationInMinutes).sum());
    }

    public void validateConflict(UUID barberId, UUID clientId, LocalDateTime startTime, LocalDateTime endTime){

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new AppointmentConflictException("Appointment cannot be scheduled in the past");
        }

        if (appointmentRepository.existsConflictingAppointment(barberId, startTime, endTime)) {
            throw new AppointmentConflictException("Barber already has an appointment in this time");
        }

        if (appointmentRepository.existsConflictingAppointmentForClient(clientId, startTime, endTime)) {
            throw new AppointmentConflictException("Client already has an appointment in this time");
        }
    }
}
