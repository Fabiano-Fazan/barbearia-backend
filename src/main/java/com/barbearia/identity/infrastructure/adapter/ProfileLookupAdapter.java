package com.barbearia.identity.infrastructure.adapter;

import com.barbearia.customer.domain.model.Client;
import com.barbearia.customer.infrastructure.persistence.ClientRepository;
import com.barbearia.identity.application.port.ProfileLookup;
import com.barbearia.staff.domain.model.Barber;
import com.barbearia.staff.infrastructure.persistence.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileLookupAdapter implements ProfileLookup {

    private final ClientRepository clientRepository;
    private final BarberRepository barberRepository;

    public Optional<UUID> findClientIdByUserId(UUID userId) {
        return clientRepository.findByUserId(userId)
                .map(Client::getId);
    }
    public Optional<UUID> findBarberIdByUserId(UUID userId) {
        return barberRepository.findByUserId(userId)
                .map(Barber::getId);
    }
}
