package com.barbearia.scheduling.domain.event;

import com.barbearia.financial.domain.model.PaymentMethod;
import com.barbearia.shared.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AppointmentCompletedEvent(

        UUID appointmentId,
        UUID barberId,
        String barberName,
        BigDecimal totalAmount,
        BigDecimal commissionRate,
        PaymentMethod paymentMethod,
        Instant occurredAt

) implements DomainEvent {}
