package com.barbearia.infrastructure.configuration;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenProvider {

   @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_EXPIRATION}")
    private long expiration;

    public String getToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();

        } else if (principal instanceof OAuth2User) {
            username = ((OAuth2User) principal).getAttribute("email");
        }

        return buildToken(username);
    }
       

    public String validateToken(String token) {
        try {

            Algorithm algorithm = Algorithm.HMAC512(secret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();

        }catch (JWTVerificationException e){
            return e.getMessage();

        }
    }

    private String buildToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        Algorithm algorithm = Algorithm.HMAC512(secret);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }
}



