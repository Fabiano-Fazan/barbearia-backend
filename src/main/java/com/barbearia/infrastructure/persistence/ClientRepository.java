package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
