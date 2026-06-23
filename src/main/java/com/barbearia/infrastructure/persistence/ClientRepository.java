package com.barbearia.infrastructure.persistence;

import com.barbearia.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Client findByName(String name);

}
