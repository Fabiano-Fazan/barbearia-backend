package com.barbearia.scheduling.domain.model;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public record AppointmentProduct(UUID productId, String name, BigDecimal price, Integer durationInMinutes) {
    public AppointmentProduct {
        if (productId == null || name == null || price == null || durationInMinutes == null || durationInMinutes <= 0) {
            throw new IllegalArgumentException("Invalid appointment product");
        }
    }
}
