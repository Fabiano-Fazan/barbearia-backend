package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BarberRepository extends JpaRepository<Barber, UUID> {

}
