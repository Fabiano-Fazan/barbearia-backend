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
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            if(oAuth2User == null) {
                throw new BadCredentialsException("Could not process authentication with Google. User data not found.");
            }
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            User user = verifyOrCreateUser(email,name);
            verifyOrCreateClient(user,name);
            String token = tokenProvider.getToken(authentication);
            String targetUrl = redirectUri + "?token=" + token;
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }catch (RuntimeException e) {
            throw new BadRequestException("Error during OAuth2 login: " + e.getMessage());
        }
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
                    .isActive(true)
                    .build();
            clientRepository.save(newClient);
        }
    }
}
