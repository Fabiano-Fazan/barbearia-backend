package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

}
