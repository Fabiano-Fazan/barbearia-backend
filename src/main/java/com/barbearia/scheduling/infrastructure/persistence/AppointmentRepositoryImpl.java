package com.barbearia.scheduling.infrastructure.persistence;

import com.barbearia.scheduling.domain.model.Appointment;
import com.barbearia.scheduling.domain.model.AppointmentStatus;
import com.barbearia.scheduling.domain.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final SpringDataAppointmentRepository springDataRepository;

    @Override
    public Appointment save(Appointment appointment) {
        return springDataRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Page<Appointment> find(UUID appointmentId, UUID clientId, UUID barberId, AppointmentStatus status, Pageable pageable
    ) {
        Specification<Appointment> specification =
                Specification.where(
                         AppointmentSpecifications.hasId(appointmentId))
                        .and(AppointmentSpecifications.hasStatus(status))
                        .and(AppointmentSpecifications.hasClient(clientId))
                        .and(AppointmentSpecifications.hasBarber(barberId));

        return springDataRepository.findAll(specification, pageable);
    }

    @Override
    public boolean existsConflictingAppointment(UUID barberId, LocalDateTime startTime, LocalDateTime endTime) {
        return springDataRepository
                .existsConflictingAppointment(barberId, startTime, endTime);
    }

    @Override
    public boolean existsConflictingAppointmentForClient(UUID clientId, LocalDateTime startTime, LocalDateTime endTime) {
        return springDataRepository
                .existsConflictingAppointmentForClient(clientId, startTime, endTime);
    }
}
