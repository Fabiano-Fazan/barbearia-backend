package com.barbearia.financial.infrastructure.persistence;

import com.barbearia.financial.domain.model.Commission;
import com.barbearia.financial.domain.model.CommissionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CommissionRepository extends JpaRepository<Commission, UUID>, JpaSpecificationExecutor<Commission> {
    Optional<Commission> findByBarberIdAndStatus(UUID barberId, CommissionStatus status);
}
