package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolesRepository extends JpaRepository<RolesEntity, UUID> {
}
