package com.barbearia.scheduling.domain.repository;

import com.barbearia.scheduling.domain.model.Appointment;
import com.barbearia.scheduling.domain.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {

        Appointment save(Appointment appointment);

        Optional<Appointment> findById(UUID id);

        Page<Appointment> find(
                UUID appointmentId,
                UUID clientId,
                UUID barberId,
                AppointmentStatus status,
                Pageable pageable
        );

        boolean existsConflictingAppointment(
                UUID barberId,
                LocalDateTime startTime,
                LocalDateTime endTime
        );

        boolean existsConflictingAppointmentForClient(
                UUID clientId,
                LocalDateTime startTime,
                LocalDateTime endTime
        );
}
