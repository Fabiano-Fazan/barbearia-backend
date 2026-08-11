package com.barbearia.financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

interface FinancialRepository extends JpaRepository<Financial, UUID>, JpaSpecificationExecutor<Financial> {
    Optional<Financial> findByAppointmentId(UUID appointmentId);
}
