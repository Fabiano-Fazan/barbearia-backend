package com.barbearia.infrastructure.api.client;

import com.barbearia.application.dto.request.ClientRequestDTO;
import com.barbearia.application.dto.response.ClientResponseDTO;
import com.barbearia.application.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UUID id,
            Pageable pageable) {
        Page<ClientResponseDTO> clients = clientService.findClients(name, phone, id, pageable);
        return ResponseEntity.ok(clients);
    }

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id, @RequestBody ClientRequestDTO clientDTO) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
