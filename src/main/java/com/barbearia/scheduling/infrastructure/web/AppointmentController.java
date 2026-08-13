package com.barbearia.scheduling.infrastructure.web;

import com.barbearia.scheduling.application.service.AppointmentApplicationService;
import com.barbearia.scheduling.domain.model.AppointmentStatus;
import com.barbearia.scheduling.application.dto.AppointmentRequestDTO;
import com.barbearia.scheduling.application.dto.AppointmentResponseDTO;
import com.barbearia.financial.domain.model.PaymentMethod;
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
class AppointmentController {

    private final AppointmentApplicationService appointmentService;

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

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER') ")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable UUID id, @RequestBody @Valid PaymentMethod paymentMethod) {
        AppointmentResponseDTO updatedAppointment = appointmentService.update(id, paymentMethod);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
