package com.barbearia.customer.infrastructure.persistence;

import com.barbearia.customer.domain.model.Client;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdForUpdate(@Param("id") UUID id);
}
