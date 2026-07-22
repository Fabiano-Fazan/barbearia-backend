package com.barbearia.infrastructure.api.barber;

import com.barbearia.application.dto.request.BarberResquestDTO;
import com.barbearia.application.dto.response.BarberResponseDTO;
import com.barbearia.application.service.BarberService;
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
@RequestMapping("/v1/barbers")
@RequiredArgsConstructor
public class BarberController {

    private final BarberService barberService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping
    public ResponseEntity<Page<BarberResponseDTO>> getBarbers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            Pageable pageable){
        Page<BarberResponseDTO> barbers = barberService.findBarbers(name, phone, pageable);
        return new ResponseEntity<>(barbers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<BarberResponseDTO> getBarberById(@PathVariable UUID id){
        BarberResponseDTO barber = barberService.findById(id);
        return new ResponseEntity<>(barber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BarberResponseDTO> createBarber(@RequestBody @Valid BarberResquestDTO barberRequest){
        BarberResponseDTO createdBarber = barberService.create(barberRequest);
        return new ResponseEntity<>(createdBarber, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<BarberResponseDTO> updateBarber(@PathVariable UUID id, @RequestBody @Valid BarberResquestDTO barberRequest){
        BarberResponseDTO updatedBarber = barberService.update(id, barberRequest);
        return new ResponseEntity<>(updatedBarber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable UUID id){
        barberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
