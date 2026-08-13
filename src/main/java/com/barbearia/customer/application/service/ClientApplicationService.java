package com.barbearia.customer.application.service;

import com.barbearia.customer.application.dto.ClientRequestDTO;
import com.barbearia.customer.application.dto.ClientResponseDTO;
import com.barbearia.customer.application.dto.ClientMapper;
import com.barbearia.customer.domain.model.Client;
import com.barbearia.customer.infrastructure.persistence.ClientRepository;
import com.barbearia.customer.infrastructure.persistence.ClientSpecifications;
import com.barbearia.identity.application.security.AuthenticatedUserProvider;
import com.barbearia.identity.domain.model.User;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.identity.application.service.UserService;
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
public class ClientApplicationService {

    private final ClientRepository clientRepository;
    private final UserService userService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ClientMapper mapper;

    @Transactional(readOnly = true)
    public Client getClientById(UUID id) throws ResourceNotFoundException {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getCurrentClient() {
        Client client = clientRepository.findById(authenticatedUserProvider.getCurrentClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return mapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> findClients(String name, String phone, UUID clientId, Pageable pageable) {
        Specification<Client> specification = Specification
                .where(ClientSpecifications.hasName(name))
                .and(ClientSpecifications.hasPhone(phone))
                .and(ClientSpecifications.hasId(clientId));
        return clientRepository.findAll(specification, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ClientResponseDTO updateCurrentClient(ClientRequestDTO clientDto) {
        Client client = clientRepository.findById(authenticatedUserProvider.getCurrentClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        processData(client, clientDto);
        return mapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponseDTO updateClient(UUID id, @Valid ClientRequestDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        processData(client, clientDTO);
        return mapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        UUID userId = client.getUserId();
        clientRepository.delete(client);
        userService.deleteUser(userId);
    }

    @Transactional
    public void deleteCurrentClient() {
        Client client = clientRepository.findById(authenticatedUserProvider.getCurrentClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        UUID userId = client.getUserId();
        clientRepository.delete(client);
        userService.deleteUser(userId);
    }

    @Transactional
    public void createFromOAuth2(User user, String name) {
        boolean clientExists = clientRepository.existsByUserId(user.getId());

        if (!clientExists) {
            Client newClient = Client.builder()
                    .name(name)
                    .phone("(XX) XXXXX-XXXX")
                    .address("Address Default")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .build();
            clientRepository.save(newClient);
        }
    }

    private void processData(Client client, ClientRequestDTO clientDto) {
        client.setName(clientDto.name());
        client.setAddress(clientDto.address());
        client.setPhone(clientDto.phone());
    }
}
