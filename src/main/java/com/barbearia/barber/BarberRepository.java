package com.barbearia.barber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

interface BarberRepository extends JpaRepository<Barber, UUID>, JpaSpecificationExecutor<Barber> {
    Optional<Barber> findByUserId(UUID userId);

}
