package com.barbearia.application.service;

import com.barbearia.application.dto.request.AppointmentRequestDTO;
import com.barbearia.application.dto.response.AppointmentResponseDTO;
import com.barbearia.application.util.AppointmentValidation;
import com.barbearia.application.util.AuthenticatedUserProvider;
import com.barbearia.domain.entities.Appointment;
import com.barbearia.domain.entities.Products;
import com.barbearia.domain.enums.AppointmentStatus;
import com.barbearia.infrastructure.persistence.AppointmentRepository;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import com.barbearia.infrastructure.persistence.specifications.AppointmentSpecifications;
import com.barbearia.shared.exceptions.ForbiddenOperationException;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private  final AppointmentRepository appointmentRepository;
    private final ProductsRepository productsRepository;
    private final AppointmentValidation appointmentValidation;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public Page<AppointmentResponseDTO> findAppointments(UUID appointmentId, UUID clientId, UUID barberId, AppointmentStatus status, Pageable pageable) {
        if(authenticatedUserProvider.isClient()){
            clientId = authenticatedUserProvider.getCurrentClient().getId();
        } else if (authenticatedUserProvider.isBarber()) {
            barberId = authenticatedUserProvider.getCurrentBarber().getId();
        }
        Specification<Appointment> specification = Specification
                .where(AppointmentSpecifications.hasId(appointmentId))
                .and(AppointmentSpecifications.hasStatus(status))
                .and(AppointmentSpecifications.hasClient(clientId))
                .and(AppointmentSpecifications.hasBarber(barberId));
        return appointmentRepository.findAll(specification, pageable)
                .map(AppointmentResponseDTO::new);
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        if (authenticatedUserProvider.isClient()
                && !authenticatedUserProvider.getCurrentClient().getId().equals(dto.clientId())) {
            throw new ForbiddenOperationException("You can only create appointments for yourself");
        }
        if (authenticatedUserProvider.isBarber()
                && !authenticatedUserProvider.getCurrentBarber().getId().equals(dto.barberId())) {
            throw new ForbiddenOperationException("You can only create appointments for yourself");
        }
        Appointment appointment = new Appointment();
        processData(appointment, dto);
        LocalDateTime endDateTime = appointmentValidation.calculateEndTime(appointment);
        appointmentValidation.validateConflict(dto.barberId(), dto.clientId(), dto.startTime(), endDateTime);
        appointment.setEndTime(endDateTime);
        appointmentRepository.save(appointment);
        return new AppointmentResponseDTO(appointment);
    }

    @Transactional
    public void  delete(UUID id) {
        Appointment appointment = canDeleteAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private void processData(Appointment appointment, AppointmentRequestDTO dto) {
        appointment.setProducts(productsRepository.findAllById(dto.productId())
                        .stream().toList());
        BigDecimal price = appointment.getProducts().stream()
                .map(Products::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        appointment.setTotalPrice(price);
        appointment.setStartTime(dto.startTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setObservation(dto.observation());
    }

    private Appointment canDeleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (authenticatedUserProvider.isClient()
                && !appointment.getClient().getId().equals(authenticatedUserProvider.getCurrentClient().getId())) {
            throw new ForbiddenOperationException("You can only cancel your own appointments");
        }
        if (authenticatedUserProvider.isBarber()
                && !appointment.getBarber().getId().equals(authenticatedUserProvider.getCurrentBarber().getId())) {
            throw new ForbiddenOperationException("You can only cancel appointments for yourself");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot delete a completed or cancelled appointment");
        }
        return appointment;
    }
}
