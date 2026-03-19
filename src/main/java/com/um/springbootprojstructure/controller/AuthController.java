package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.*;
import com.um.springbootprojstructure.service.AuthService;
import com.um.springbootprojstructure.service.PasswordResetService;
import com.um.springbootprojstructure.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, AuthService authService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeterministicDecisionResponse> register(@Valid @RequestBody RegisterRequest req) {
        try {
            userService.registerUser(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new DeterministicDecisionResponse(true));
        } catch (IllegalArgumentException ex) {
            // deterministic: do not reveal whether email existed or password failed policy
            return ResponseEntity.ok(new DeterministicDecisionResponse(false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.getEmail(), req.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(Authentication authentication) {
        return ResponseEntity.ok(authService.refreshFor(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        if (authentication == null) {
            throw new IllegalArgumentException("invalid_credentials");
        }
        String jwt = extractBearer(authorizationHeader);
        authService.logoutCurrentToken(jwt);
        return ResponseEntity.ok(new LogoutResponse(true));
    }

    // Password reset endpoints
    @PostMapping("/password-reset/request")
    public ResponseEntity<DeterministicDecisionResponse> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest req
    ) {
        // deterministic response regardless of whether user exists
        passwordResetService.requestReset(req.getEmail());
        return ResponseEntity.ok(new DeterministicDecisionResponse(true));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<DeterministicDecisionResponse> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest req
    ) {
        boolean ok = passwordResetService.confirmReset(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new DeterministicDecisionResponse(ok));
    }

    private String extractBearer(String header) {
        if (header == null) throw new IllegalArgumentException("invalid_credentials");
        if (!header.startsWith("Bearer ")) throw new IllegalArgumentException("invalid_credentials");
        String token = header.substring("Bearer ".length()).trim();
        if (token.isEmpty()) throw new IllegalArgumentException("invalid_credentials");
        return token;
    }
}
