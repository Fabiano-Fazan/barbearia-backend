package com.barbearia.auth;

import com.barbearia.client.ClientService;
import com.barbearia.role.Role;
import com.barbearia.role.RoleService;
import com.barbearia.user.User;
import com.barbearia.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final ClientService clientService;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;

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
            Role roles = roleService.findByName("ROLE_CLIENT");
            User user = userService.createUserByClient(name, email, roles);
            clientService.createFromOAuth2(user, name);
            String token = tokenProvider.getToken(authentication);
            String targetUrl = redirectUri + "?token=" + token;
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }catch (RuntimeException e) {
            throw new BadRequestException("Error during OAuth2 login: " + e.getMessage());
        }
    }
}
