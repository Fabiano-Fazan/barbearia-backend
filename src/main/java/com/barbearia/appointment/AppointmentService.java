package com.barbearia.appointment;

import com.barbearia.appointment.dto.AppointmentRequestDTO;
import com.barbearia.appointment.dto.AppointmentResponseDTO;
import com.barbearia.financial.FinancialService;
import com.barbearia.auth.AuthenticatedUserProvider;
import com.barbearia.product.Product;
import com.barbearia.financial.PaymentMethod;
import com.barbearia.core.exceptions.ForbiddenOperationException;
import com.barbearia.core.exceptions.ResourceNotFoundException;
import com.barbearia.product.ProductService;
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
    private final ProductService productService;
    private final AppointmentValidation appointmentValidation;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final FinancialService financialService;


    @Transactional(readOnly = true)
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

    public Appointment getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
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
    public AppointmentResponseDTO update(UUID appointmentId, PaymentMethod paymentMethod) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (authenticatedUserProvider.isBarber()
                && !appointment.getBarber().getId().equals(authenticatedUserProvider.getCurrentBarber().getId())) {
            throw new ForbiddenOperationException("You can only update appointments for yourself");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        financialService.processFinancialByAppointment(appointment, paymentMethod);
        financialService.processCommissionByAppointment(appointment);
        return new AppointmentResponseDTO(appointment);
    }

    @Transactional
    public void  delete(UUID id) {
        Appointment appointment = canDeleteAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private void processData(Appointment appointment, AppointmentRequestDTO dto) {
        appointment.setProducts(productService.getAllProductsById(dto.productId()));
        BigDecimal price = appointment.getProducts().stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        appointment.setTotalPrice(price);
        appointment.setStartTime(dto.startTime() != null ? dto.startTime() : LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setObservation(dto.observation());
    }


    private Appointment canDeleteAppointment(UUID id) {
        Appointment appointment = this.getAppointmentById(id);
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
