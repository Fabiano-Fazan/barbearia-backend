package com.barbearia.application.service;

import com.barbearia.application.dto.request.ClientRequestDTO;
import com.barbearia.application.dto.response.ClientResponseDTO;
import com.barbearia.domain.entities.Client;
import com.barbearia.infrastructure.persistence.ClientRepository;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Page<ClientResponseDTO> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(ClientResponseDTO::new);
    }

    public ClientResponseDTO findById(UUID id) {
        return clientRepository.findById(id)
                .map(ClientResponseDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    public Page<ClientResponseDTO> findByName(String name, Pageable pageable) {
        return clientRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ClientResponseDTO::new);
    }

    public ClientResponseDTO findByEmail(String email) {
        return clientRepository.findAll().stream()
                .filter(client -> client.getUser().getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(ClientResponseDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    @Transactional
    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO clientDto) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        processData(client, clientDto);
        clientRepository.save(client);
        return new ClientResponseDTO(client);
    }

    @Transactional
    public void deleteClient(UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        clientRepository.delete(client);
    }

    private void processData(Client client, ClientRequestDTO clientDto) {

        client.setName(clientDto.name());
        client.setAddress(clientDto.address());
        client.setPhone(clientDto.phone());
        client.setUpdatedAt(LocalDateTime.now());

    }
}
