package com.barbearia.application.service;

import com.barbearia.application.dto.request.ClientRequestDTO;
import com.barbearia.application.dto.response.ClientResponseDTO;
import com.barbearia.application.util.AuthenticatedUserProvider;
import com.barbearia.domain.entities.Client;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.persistence.ClientRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import com.barbearia.infrastructure.persistence.specifications.ClientSpecifications;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
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
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public ClientResponseDTO getCurrentClient(){
        Client client = authenticatedUserProvider.getCurrentClient();
        return new ClientResponseDTO(client);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> findClients(String name, String phone, UUID clientId, Pageable pageable) {
        Specification<Client> specification = Specification
                .where(ClientSpecifications.hasName(name))
                .and(ClientSpecifications.hasPhone(phone))
                .and(ClientSpecifications.hasId(clientId));
        return clientRepository.findAll(specification, pageable)
                .map(ClientResponseDTO::new);
    }

    @Transactional
    public ClientResponseDTO updateCurrentClient(ClientRequestDTO clientDto) {
        Client client = authenticatedUserProvider.getCurrentClient();
        processData(client, clientDto);
        return new ClientResponseDTO(clientRepository.save(client));
    }

    @Transactional
    public ClientResponseDTO updateClient(UUID id, @Valid ClientRequestDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        processData(client, clientDTO);
        return new ClientResponseDTO(clientRepository.save(client));
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

    @Transactional
    public void deleteCurrentClient() {
        Client client = authenticatedUserProvider.getCurrentClient();
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
    }
}
