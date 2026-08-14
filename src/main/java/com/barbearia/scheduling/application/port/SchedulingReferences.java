package com.barbearia.scheduling.application.port;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SchedulingReferences {

    ClientData getClient(UUID id);
    BarberData getBarber(UUID id);
    List<ProductData> getProducts(List<UUID> ids);
    record ClientData(UUID id, String name) {}
    record BarberData(UUID id, String name, BigDecimal commissionRate) {}
    record ProductData(UUID id, String name, BigDecimal price, int durationInMinutes) {}
    void lockBarber(UUID barberId);
    void lockClient(UUID clientId);
}
