package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.config.JwtService;
import com.um.springbootprojstructure.dto.AuthResponse;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRevocationService tokenRevocationService;

    public AuthService(UserRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       TokenRevocationService tokenRevocationService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        // prevent user enumeration: same error for missing user vs bad password
        User u = users.findByEmailIgnoreCase(email).orElse(null);

        boolean ok = u != null
                && u.isEnabled()
                && passwordEncoder.matches(password, u.getPasswordHash());

        if (!ok) {
            throw new IllegalArgumentException("invalid_credentials");
        }

        String jwt = jwtService.issueToken(u.getEmail(), u.getRole());
        return new AuthResponse(jwt, jwtService.getTtlSeconds());
    }

    /**
     * Issues a new access token for an authenticated session context.
     * Uses current authenticated identity, and re-derives role from DB.
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshFor(String authenticatedEmail) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            // Fail closed
            throw new IllegalArgumentException("invalid_credentials");
        }

        User u = users.findByEmailIgnoreCase(authenticatedEmail).orElse(null);
        if (u == null || !u.isEnabled()) {
            // deterministic response, do not reveal whether user exists
            throw new IllegalArgumentException("invalid_credentials");
        }

        String jwt = jwtService.issueToken(u.getEmail(), u.getRole());
        return new AuthResponse(jwt, jwtService.getTtlSeconds());
    }

    @Transactional
    public void logoutCurrentToken(String rawJwt) {
        if (rawJwt == null || rawJwt.isBlank()) {
            throw new IllegalArgumentException("invalid_credentials");
        }

        Claims claims = jwtService.parseAndValidate(rawJwt);

        String jti = claims.getId();
        Date exp = claims.getExpiration();
        if (jti == null || jti.isBlank() || exp == null) {
            // fail closed: treat as invalid
            throw new IllegalArgumentException("invalid_credentials");
        }

        tokenRevocationService.revoke(jti, Instant.ofEpochMilli(exp.getTime()));
    }
}
