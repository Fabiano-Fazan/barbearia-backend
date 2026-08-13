package com.barbearia.scheduling.domain.model;

import com.barbearia.shared.domain.exception.BusinessException;
import com.barbearia.financial.domain.model.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String observation;

    @Embedded
    private TimeSlot timeSlot;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "barber_id", nullable = false)
    private UUID barberId;

    @Column(name = "barber_name", nullable = false)
    private String barberName;

    @ElementCollection
    @CollectionTable(name = "appointment_products", joinColumns = @JoinColumn(name = "appointment_id"))
    private List<AppointmentProduct> products = new ArrayList<>();

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    protected Appointment() {}

    public Appointment(UUID clientId, String clientName, UUID barberId, String barberName,
                       List<AppointmentProduct> products, TimeSlot timeSlot, String observation) {
        if (clientId == null || barberId == null || clientName == null || barberName == null || products == null || products.isEmpty()) {
            throw new BusinessException("Appointment requires client, barber and products");
        }
        this.clientId = clientId;
        this.clientName = clientName;
        this.barberId = barberId;
        this.barberName = barberName;
        this.products = new ArrayList<>(products);
        this.timeSlot = timeSlot;
        this.observation = observation;
        this.totalPrice = products.stream().map(AppointmentProduct::price).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.status = AppointmentStatus.SCHEDULED;
    }

    public void complete(PaymentMethod paymentMethod) {
        if (paymentMethod == null) throw new BusinessException("Payment method is required");
        if (status != AppointmentStatus.SCHEDULED) throw new BusinessException("Appointment cannot be completed");
        status = AppointmentStatus.COMPLETED;
    }

    public void cancel() {
        if (status != AppointmentStatus.SCHEDULED) throw new BusinessException("Appointment cannot be cancelled");
        status = AppointmentStatus.CANCELLED;
    }

    public LocalDateTime getStartTime() {
        return timeSlot.startTime();
    }
    public LocalDateTime getEndTime() {
        return timeSlot.endTime();
    }
}
