package com.barbearia.barber;

import com.barbearia.barber.dto.BarberRequestDTO;
import com.barbearia.barber.dto.BarberResponseDTO;
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

    @GetMapping("/me")
    public ResponseEntity<BarberResponseDTO> getCurrentBarber() {
        BarberResponseDTO barber = barberService.getCurrentBarber();
        return new ResponseEntity<>(barber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<BarberResponseDTO>> getBarbers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UUID id,
            Pageable pageable){
        Page<BarberResponseDTO> barbers = barberService.findBarbers(name, phone, id, pageable);
        return new ResponseEntity<>(barbers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BarberResponseDTO> createBarber(@RequestBody @Valid BarberRequestDTO barberRequest){
        BarberResponseDTO createdBarber = barberService.create(barberRequest);
        return new ResponseEntity<>(createdBarber, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BarberResponseDTO> updateBarber(@PathVariable UUID id, @RequestBody @Valid BarberRequestDTO barberRequest){
        BarberResponseDTO updatedBarber = barberService.update(id, barberRequest);
        return new ResponseEntity<>(updatedBarber, HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<BarberResponseDTO> updateCurrentBarber(@RequestBody @Valid BarberRequestDTO barberRequest){
        BarberResponseDTO updatedBarber = barberService.updateCurrentBarber(barberRequest);
        return new ResponseEntity<>(updatedBarber, HttpStatus.OK);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentBarber(){
        barberService.deleteCurrentBarber();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable UUID id){
        barberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
