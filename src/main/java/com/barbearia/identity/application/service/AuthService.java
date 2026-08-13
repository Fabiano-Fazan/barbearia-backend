package com.barbearia.identity.application.service;

import com.barbearia.identity.application.dto.LoginRequestDTO;
import com.barbearia.identity.application.dto.RegisterRequestDTO;
import com.barbearia.identity.application.dto.TokenResponseDTO;
import com.barbearia.shared.domain.exception.EntityAlreadyExistsException;
import com.barbearia.identity.domain.model.Role;
import com.barbearia.identity.domain.model.User;
import com.barbearia.identity.infrastructure.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Transactional
    public void registerAdmin(RegisterRequestDTO dto) {

        if(userService.existsByEmail(dto.email())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        Role roles = roleService.findByName("ROLE_ADMIN");
        userService.createUserByAdmin(dto, roles);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) throws BadCredentialsException {
        try{
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()
                    ));
            User user = (User) authenticate.getPrincipal();

            if (user == null){
                throw new BadCredentialsException("Invalid credentials");
            }
            String token = tokenProvider.getToken(authenticate);

            return new TokenResponseDTO(token, user.isMustChangePassword());

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
