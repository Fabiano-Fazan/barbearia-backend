package com.barbearia.infrastructure.api.client;

import com.barbearia.application.dto.request.ClientRequestDTO;
import com.barbearia.application.dto.response.ClientResponseDTO;
import com.barbearia.application.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{me}")
    public ResponseEntity<ClientResponseDTO> getCurrentClient() {
        ClientResponseDTO client = clientService.getCurrentClient();
        return ResponseEntity.ok(client);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UUID id,
            Pageable pageable) {
        Page<ClientResponseDTO> clients = clientService.findClients(name, phone, id, pageable);
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }


    @PutMapping("/{me}")
    public ResponseEntity<ClientResponseDTO> updateCurrentClient(@RequestBody @Valid ClientRequestDTO clientDTO) {
        ClientResponseDTO updatedCurrentClient = clientService.updateCurrentClient(clientDTO);
        return new ResponseEntity<>(updatedCurrentClient, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable UUID id, @RequestBody @Valid ClientRequestDTO clientDTO) {
       ClientResponseDTO updatedClient = clientService.updateClient(id, clientDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
