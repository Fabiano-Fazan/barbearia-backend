package com.barbearia.auth;

import com.barbearia.barber.Barber;
import com.barbearia.barber.BarberService;
import com.barbearia.client.Client;
import com.barbearia.client.ClientService;
import com.barbearia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final ClientService clientService;
    private final BarberService barberService;

    public User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    public Client getCurrentClient() {
        return clientService.getClientByUserId(getCurrentUser().getId());
    }

    public Barber getCurrentBarber() {
        return barberService.getBarberByUserId(getCurrentUser().getId());
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
