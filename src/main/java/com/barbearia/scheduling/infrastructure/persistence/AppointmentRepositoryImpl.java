package com.barbearia.scheduling.infrastructure.persistence;

import com.barbearia.scheduling.domain.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final SpringDataAppointmentRepository springDataRepo;


    @Override
    public boolean existsConflictingAppointment(UUID barberId, LocalDateTime startTime, LocalDateTime endTime) {
        return springDataRepo.existsConflictingAppointment(barberId, startTime, endTime);
    }

    @Override
    public boolean existsConflictingAppointmentForClient(UUID clientId, LocalDateTime startTime, LocalDateTime endTime) {
        return springDataRepo.existsConflictingAppointmentForClient(clientId, startTime, endTime);
    }
}
