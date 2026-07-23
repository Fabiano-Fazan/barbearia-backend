package com.barbearia.application.service;

import com.barbearia.application.dto.request.AppointmentRequestDTO;
import com.barbearia.application.dto.response.AppointmentResponseDTO;
import com.barbearia.application.util.AppointmentValidation;
import com.barbearia.domain.entities.Appointment;
import com.barbearia.domain.enums.AppointmentStatus;
import com.barbearia.infrastructure.persistence.AppointmentRepository;
import com.barbearia.infrastructure.persistence.BarberRepository;
import com.barbearia.infrastructure.persistence.ClientRepository;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import com.barbearia.infrastructure.persistence.specifications.AppointmentSpecifications;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private  final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;
    private final ClientRepository clientRepository;
    private final ProductsRepository productsRepository;
    private final AppointmentValidation appointmentValidation;

    public Page<AppointmentResponseDTO> findAppointments(UUID appointmentId, UUID clientId, UUID barberId, Pageable pageable) {
        Specification<Appointment> specification = Specification
                .where(AppointmentSpecifications.hasId(appointmentId))
                .and(AppointmentSpecifications.hasClient(clientId))
                .and(AppointmentSpecifications.hasBarber(barberId));
        return appointmentRepository.findAll(specification, pageable)
                .map(AppointmentResponseDTO::new);
    }

    public Page<AppointmentResponseDTO> findAppointmentsByStatus(Pageable pageable) {
        Specification<Appointment> specification = Specification
                .where(AppointmentSpecifications.hasStatus(AppointmentStatus.SCHEDULED))
                .and(AppointmentSpecifications.hasStatus(AppointmentStatus.CANCELLED))
                .and(AppointmentSpecifications.hasStatus(AppointmentStatus.COMPLETED));
        return appointmentRepository.findAll(specification, pageable)
                .map(AppointmentResponseDTO::new);
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        processData(appointment, dto);
        LocalDateTime endDateTime = appointmentValidation.calculateEndTime(dto.startTime(), appointment.getProducts());
        appointmentValidation.validateConflict(dto.barberId(), dto.clientId(), dto.startTime(), endDateTime);
        appointmentRepository.save(appointment);
        return new AppointmentResponseDTO(appointment);
    }

    @Transactional
    public void  delete(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot delete a completed appointment");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private void processData(Appointment appointment, AppointmentRequestDTO dto) {
        appointment.setBarber(barberRepository.findById(dto.barberId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found")));
        appointment.setClient(clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found")));
        appointment.setProducts(productsRepository.findById(dto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        appointment.setStartTime(dto.startTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setObservation(dto.observation());
    }
}
