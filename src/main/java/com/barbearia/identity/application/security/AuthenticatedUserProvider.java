package com.barbearia.identity.application.security;

import com.barbearia.identity.application.port.ProfileLookup;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import com.barbearia.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final ProfileLookup profileLookup;

    public User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    public java.util.UUID getCurrentClientId() {
        return profileLookup.findClientIdByUserId(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    public java.util.UUID getCurrentBarberId() {
        return profileLookup.findBarberIdByUserId(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
    }

    public boolean isClient() {
        return hasRole("ROLE_CLIENT");
    }
    public boolean isBarber() {
        return hasRole("ROLE_BARBER");
    }

    private boolean hasRole(String role) {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), role));
    }
}
