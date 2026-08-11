package com.barbearia.auth;

import com.barbearia.barber.Barber;
import com.barbearia.client.Client;
import com.barbearia.barber.BarberRepository;
import com.barbearia.client.ClientRepository;
import com.barbearia.core.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final ClientRepository clientRepository;
    private final BarberRepository barberRepository;

    public User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    public Client getCurrentClient() {
        return clientRepository.findByUserId(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found for logged user"));
    }

    public Barber getCurrentBarber() {
        return barberRepository.findByUserId(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found for logged user"));
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
