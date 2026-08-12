package com.barbearia.client;

import com.barbearia.client.dto.ClientRequestDTO;
import com.barbearia.client.dto.ClientResponseDTO;
import com.barbearia.auth.AuthenticatedUserProvider;
import com.barbearia.user.User;
import com.barbearia.core.exceptions.ResourceNotFoundException;
import com.barbearia.user.UserService;
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
    private final UserService userService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public ClientResponseDTO getCurrentClient() {
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
        clientRepository.delete(client);
        userService.deleteUser(user.getId());
    }

    @Transactional
    public void deleteCurrentClient() {
        Client client = authenticatedUserProvider.getCurrentClient();
        User user = client.getUser();
        clientRepository.delete(client);
        userService.deleteUser(user.getId());
    }

    @Transactional
    public void createFromOAuth2(User user, String name) {
        boolean clientExists = clientRepository.existsByUserId(user.getId());

        if (!clientExists) {
            Client newClient = Client.builder()
                    .name(name)
                    .phone("(XX) XXXXX-XXXX")
                    .address("Address Default")
                    .user(user)
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
