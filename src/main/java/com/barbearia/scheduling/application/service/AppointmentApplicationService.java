package com.barbearia.scheduling.application.service;

import com.barbearia.scheduling.application.dto.*;
import com.barbearia.scheduling.domain.event.AppointmentCompletedEvent;
import com.barbearia.scheduling.domain.model.*;
import com.barbearia.scheduling.domain.service.AppointmentConflictChecker;
import com.barbearia.scheduling.infrastructure.persistence.AppointmentSpecifications;
import com.barbearia.scheduling.infrastructure.persistence.SpringDataAppointmentRepository;
import com.barbearia.identity.application.security.AuthenticatedUserProvider;
import com.barbearia.scheduling.application.port.SchedulingReferences;
import com.barbearia.shared.domain.exception.ForbiddenOperationException;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.financial.domain.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentApplicationService {

    private final SpringDataAppointmentRepository repository;
    private final SchedulingReferences references;
    private final AppointmentConflictChecker conflictChecker;
    private final AuthenticatedUserProvider currentUser;
    private final AppointmentMapper mapper;
    private final ApplicationEventPublisher events;

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findAppointments(UUID id, UUID clientId, UUID barberId, AppointmentStatus status, Pageable pageable) {
        if (currentUser.isClient()) clientId = currentUser.getCurrentClientId();
        else if (currentUser.isBarber()) barberId = currentUser.getCurrentBarberId();
        Specification<Appointment> spec = Specification.where(AppointmentSpecifications.hasId(id))
                .and(AppointmentSpecifications.hasStatus(status))
                .and(AppointmentSpecifications.hasClient(clientId))
                .and(AppointmentSpecifications.hasBarber(barberId));
        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        authorizeCreate(dto);
        references.lockBarber(dto.barberId());
        references.lockClient(dto.clientId());
        var client = references.getClient(dto.clientId());
        var barber = references.getBarber(dto.barberId());
        var products = references.getProducts(dto.productIds());
        if (products.size() != dto.productIds().stream().distinct().count()) throw new ResourceNotFoundException("One or more products not found");
        List<AppointmentProduct> snapshots = products.stream().map(p ->
                new AppointmentProduct(p.id(), p.name(), p.price(), p.durationInMinutes())).toList();
        int duration = snapshots.stream()
                .mapToInt(AppointmentProduct::durationInMinutes).sum();
        TimeSlot slot = new TimeSlot(dto.startTime(), dto.startTime().plusMinutes(duration));
        conflictChecker.ensureNoConflict(dto.barberId(), dto.clientId(), slot);
        return mapper.toResponse(repository.save(new Appointment(client.id(), client.name(), barber.id(),
                barber.name(), snapshots, slot, dto.observation())));
    }

    @Transactional
    public AppointmentResponseDTO update(UUID id, PaymentMethod paymentMethod) {
        Appointment appointment = getAppointmentById(id);
        authorizeBarber(appointment);
        appointment.complete(paymentMethod);
        repository.save(appointment);
        BigDecimal rate = currentCommissionRate(appointment.getBarberId());
        events.publishEvent(new AppointmentCompletedEvent(appointment.getId(), appointment.getBarberId(),
                appointment.getBarberName(), appointment.getTotalPrice(), rate, paymentMethod, Instant.now()));
        return mapper.toResponse(appointment);
    }

    @Transactional
    public void delete(UUID id) {
        Appointment appointment = getAppointmentById(id);
        authorizeOwner(appointment);
        appointment.cancel();
        repository.save(appointment);
    }

    private BigDecimal currentCommissionRate(UUID barberId) {
        BigDecimal rate = references.getBarber(barberId).commissionRate();
        return rate == null ? new BigDecimal("50") : rate;
    }
    private void authorizeCreate(AppointmentRequestDTO dto) {
        if (currentUser.isClient() && !currentUser.getCurrentClientId().equals(dto.clientId()))
            throw new ForbiddenOperationException("You can only create appointments for yourself");
        if (currentUser.isBarber() && !currentUser.getCurrentBarberId().equals(dto.barberId()))
            throw new ForbiddenOperationException("You can only create appointments for yourself");
    }
    private void authorizeBarber(Appointment appointment) {
        if (currentUser.isBarber() && !appointment.getBarberId().equals(currentUser.getCurrentBarberId()))
            throw new ForbiddenOperationException("You can only update appointments for yourself");
    }
    private void authorizeOwner(Appointment appointment) {
        if (currentUser.isClient() && !appointment.getClientId().equals(currentUser.getCurrentClientId()))
            throw new ForbiddenOperationException("You can only cancel your own appointments");
        authorizeBarber(appointment);
    }
}
