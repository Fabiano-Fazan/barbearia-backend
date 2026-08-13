package com.barbearia.identity.application.port;

import java.util.Optional;
import java.util.UUID;

public interface ProfileLookup {
    Optional<UUID> findClientIdByUserId(UUID userId);
    Optional<UUID> findBarberIdByUserId(UUID userId);
}
