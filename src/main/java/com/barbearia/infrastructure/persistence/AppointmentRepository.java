package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    @Query("""

            SELECT EXISTS (
            SELECT 1 FROM Appointment a
            WHERE a.barber.id = :barberId
            AND a.startTime < :endTime
            AND a.endTime > :startTime
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
            WHERE a.client.id = :clientId
            AND a.startTime < :endTime
            AND a.endTime > :startTime
        )
        """)
    boolean existsConflictingAppointmentForClient(
        @Param("clientId") UUID clientId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}