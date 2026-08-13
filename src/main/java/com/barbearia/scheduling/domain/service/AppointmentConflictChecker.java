package com.barbearia.scheduling.domain.service;

import com.barbearia.scheduling.domain.model.TimeSlot;
import com.barbearia.scheduling.domain.repository.AppointmentRepository;
import com.barbearia.shared.domain.exception.AppointmentConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentConflictChecker {

    private final AppointmentRepository repository;

    public void ensureNoConflict(UUID barberId, UUID clientId, TimeSlot slot) {

        if (slot.startTime().isBefore(LocalDateTime.now(Clock.systemDefaultZone())))
            throw new AppointmentConflictException("Appointment cannot be scheduled in the past");

        if (repository.existsConflictingAppointment(barberId, slot.startTime(), slot.endTime()))
            throw new AppointmentConflictException("Barber already has an appointment in this time");

        if (repository.existsConflictingAppointmentForClient(clientId, slot.startTime(), slot.endTime()))
            throw new AppointmentConflictException("Client already has an appointment in this time");
    }
}
