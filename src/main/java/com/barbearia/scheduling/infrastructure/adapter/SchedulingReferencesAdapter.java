package com.barbearia.scheduling.infrastructure.adapter;

import com.barbearia.catalog.application.service.CatalogApplicationService;
import com.barbearia.customer.application.service.ClientApplicationService;
import com.barbearia.scheduling.application.port.SchedulingReferences;
import com.barbearia.staff.application.service.BarberApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SchedulingReferencesAdapter implements SchedulingReferences {

    private final ClientApplicationService clientService;
    private final BarberApplicationService barberService;
    private final CatalogApplicationService catalogService;

    public ClientData getClient(UUID id) {

        var value = clientService.getClientById(id);
        return new ClientData(value.getId(), value.getName());
    }
    public BarberData getBarber(UUID id) {
        var value = barberService.getBarberById(id);
        return new BarberData(value.getId(), value.getName(), value.getCommissionRate());
    }
    public List<ProductData> getProducts(List<UUID> ids) {
        return catalogService.getAllProductsById(ids).stream()
                .map(p -> new ProductData(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getDurationInMinutes()))
                        .toList();
    }
}
