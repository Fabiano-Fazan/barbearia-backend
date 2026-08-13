package com.barbearia.scheduling.infrastructure.persistence;

import com.barbearia.scheduling.domain.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataAppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    @Query("""
            SELECT EXISTS (
            SELECT 1 FROM Appointment a
            WHERE a.barberId = :barberId
            AND a.status = com.barbearia.scheduling.domain.model.AppointmentStatus.SCHEDULED
            AND a.timeSlot.startTime < :endTime
            AND a.timeSlot.endTime > :startTime
        )
        """)
    boolean existsConflictingAppointment(
        @Param("barberId") UUID barberId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("""
            SELECT EXISTS (
            SELECT 1 FROM Appointment a
            WHERE a.clientId = :clientId
            AND a.status = com.barbearia.scheduling.domain.model.AppointmentStatus.SCHEDULED
            AND a.timeSlot.startTime < :endTime
            AND a.timeSlot.endTime > :startTime
        )
        """)
    boolean existsConflictingAppointmentForClient(
        @Param("clientId") UUID clientId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
