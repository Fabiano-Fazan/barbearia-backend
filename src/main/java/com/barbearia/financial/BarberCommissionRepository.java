package com.barbearia.financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BarberCommissionRepository extends JpaRepository<BarberCommission, UUID>, JpaSpecificationExecutor<BarberCommission> {
    Optional<BarberCommission> findByBarberIdAndStatus(UUID barberId, CommissionStatus status);
}
