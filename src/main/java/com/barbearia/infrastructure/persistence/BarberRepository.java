package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BarberRepository extends JpaRepository<Barber, UUID>, JpaSpecificationExecutor<Barber> {

}
