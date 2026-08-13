package com.barbearia.scheduling.domain.repository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentRepository{

    boolean existsConflictingAppointment(UUID barberId, LocalDateTime startTime, LocalDateTime endTime);
    boolean existsConflictingAppointmentForClient(UUID clientId, LocalDateTime startTime, LocalDateTime endTime);
}
