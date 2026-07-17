package com.barbearia.infrastructure.configuration;

import com.barbearia.domain.entities.Client;
import com.barbearia.domain.entities.RolesEntity;
import com.barbearia.domain.entities.User;
import com.barbearia.infrastructure.persistence.ClientRepository;
import com.barbearia.infrastructure.persistence.RolesRepository;
import com.barbearia.infrastructure.persistence.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        assert oAuth2User != null;
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = verifyOrCreateUser(email,name);
        verifyOrCreateClient(user,name);
        String token = tokenProvider.getToken(authentication);
        String targetUrl = "http://localhost:3000/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    private User verifyOrCreateUser(String email, String name){
        return userRepository.findByEmail(email).orElseGet(() -> {
           RolesEntity clientRole = rolesRepository.findByName("ROLE_CLIENT")
                   .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                           .name("ROLE_CLIENT")
                           .build()));

           User newUser = User.builder()
                   .email(email)
                   .name(name)
                   .password(passwordEncoder.encode("defaultPassword"))
                   .roles(Set.of(clientRole))
                   .build();

           return userRepository.save(newUser);
       });
    }

    private void verifyOrCreateClient(User user, String name){
        boolean clientExists = clientRepository.findAll().stream()
                .anyMatch(client -> client.getUser() != null && client.getUser().getId().equals(user.getId()));
        if (!clientExists) {
            Client newClient = Client.builder()
                    .name(name)
                    .phone("(XX) XXXXX-XXXX")
                    .address("Address Default")
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .isActive(true)
                    .build();
            clientRepository.save(newClient);
        }
    }
}
