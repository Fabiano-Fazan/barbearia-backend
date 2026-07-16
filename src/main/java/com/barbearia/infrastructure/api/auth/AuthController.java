package com.barbearia.infrastructure.api.auth;

import com.barbearia.application.dto.request.LoginRequestDTO;
import com.barbearia.application.dto.request.RegisterRequestDTO;
import com.barbearia.application.dto.response.TokenResponseDTO;
import com.barbearia.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-admin")
    public ResponseEntity<Void> registerAdmin(@RequestBody @Valid RegisterRequestDTO dto) throws BadRequestException {
        authService.registerAdmin(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-admin")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) throws BadCredentialsException {
        TokenResponseDTO token = authService.login(dto);
        return ResponseEntity.ok(token);
    }
}
