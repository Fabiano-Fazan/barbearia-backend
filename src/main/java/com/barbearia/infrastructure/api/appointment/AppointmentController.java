package com.barbearia.infrastructure.api.appointment;

import com.barbearia.application.dto.request.AppointmentRequestDTO;
import com.barbearia.application.dto.response.AppointmentResponseDTO;
import com.barbearia.application.service.AppointmentService;
import com.barbearia.domain.enums.AppointmentStatus;
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
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> getAppointments(
            @RequestParam (required = false) UUID appointmentId,
            @RequestParam (required = false) UUID clientId,
            @RequestParam (required = false) UUID barberId,
            @RequestParam (required = false) AppointmentStatus status,
            Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findAppointments(appointmentId, clientId, barberId, status, pageable);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
        AppointmentResponseDTO createdAppointment = appointmentService.create(appointmentRequestDTO);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
