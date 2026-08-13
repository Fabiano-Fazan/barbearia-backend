package com.barbearia.staff.infrastructure.persistence;

import com.barbearia.staff.domain.model.Barber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BarberRepository extends JpaRepository<Barber, UUID>, JpaSpecificationExecutor<Barber> {
    Optional<Barber> findByUserId(UUID userId);

}
