package com.barbearia.application.service;

import com.barbearia.application.dto.request.ClientRequestDTO;
import com.barbearia.application.dto.response.ClientResponseDTO;
import com.barbearia.domain.entities.Client;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.persistence.ClientRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import com.barbearia.infrastructure.persistence.specifications.ClientSpecifications;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;


    public Page<ClientResponseDTO> findClients(String name, String phone, UUID clientId, Pageable pageable) {
        Specification<Client> specification = Specification
                .where(ClientSpecifications.hasName(name))
                .and(ClientSpecifications.hasPhone(phone))
                .and(ClientSpecifications.hasId(clientId));
        return clientRepository.findAll(specification, pageable)
                .map(ClientResponseDTO::new);
    }

    @Transactional
    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO clientDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        processData(client, clientDto);
        clientRepository.save(client);
        return new ClientResponseDTO(client);
    }

    @Transactional
    public void deleteClient(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        User user = client.getUser();
        client.setIsActive(false);
        user.setIsActive(false);
        clientRepository.save(client);
        userRepository.save(user);
    }

    private void processData(Client client, ClientRequestDTO clientDto) {
        client.setName(clientDto.name());
        client.setAddress(clientDto.address());
        client.setPhone(clientDto.phone());
        client.setIsActive(clientDto.isActive());
    }
}
